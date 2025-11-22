package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.*;
import br.com.louise.AppProdutos.model.*;
import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.repository.UserRepository;
import br.com.louise.AppProdutos.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderEntityRepository orderEntityRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository; // Agora injetamos o repositório de produtos!

    private static final List<OrderStatus> CANCELLATION_ALLOWED_STATUSES = Arrays.asList(
            OrderStatus.CREATED,
            OrderStatus.PAID
    );

    @Override
    public OrderResponse createOrder(OrderRequest request) {
        // 1. Identifica o Cliente Logado
        String customerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Cliente logado não encontrado."));

        // 2. Processa os itens do carrinho (Busca preço REAL no banco)
        List<OrderProductEntity> orderProducts = request.getCartProducts().stream()
                .map(itemRequest -> {
                    // Busca o produto original para pegar preço e nome atuais
                    ProductEntity product = productRepository.findByProductId(itemRequest.getProductId())
                            .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + itemRequest.getProductId()));

                    // Validação de Estoque (Opcional, mas recomendado)
                    if (product.getStockQuantity() < itemRequest.getQuantity()) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente para o produto: " + product.getName());
                    }

                    // Cria o item do pedido com os dados "congelados" (Snapshot)
                    return OrderProductEntity.builder()
                            .productId(product.getProductId())
                            .name(product.getName())
                            .price(product.getPrice()) // Preço REAL do banco
                            .quantity(itemRequest.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        // 3. Calcula Totais
        BigDecimal subTotal = orderProducts.stream()
                .map(p -> p.getPrice().multiply(new BigDecimal(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Exemplo: Taxa fixa de 10%
        BigDecimal tax = subTotal.multiply(new BigDecimal("0.10"));
        BigDecimal grandTotal = subTotal.add(tax);

        // 4. Monta o Pedido
        OrderEntity newOrder = OrderEntity.builder()
                .customer(customer)
                .customerName(request.getCustomerName() != null ? request.getCustomerName() : customer.getName())
                .phoneNumber(request.getPhoneNumber())
                .status(OrderStatus.CREATED)
                .paymentMethod(request.getPaymentMethod())
                .tax(tax)
                .grandTotal(grandTotal)
                .products(orderProducts) // JPA Cascade vai salvar os itens
                .build();

        // Configura detalhes de pagamento iniciais
        DTOPaymentDetails paymentDetails = new DTOPaymentDetails();
        boolean isInstant = request.getPaymentMethod() == PaymentMethod.PIX || request.getPaymentMethod() == PaymentMethod.DINHEIRO;
        paymentDetails.setStatus(isInstant ? DTOPaymentDetails.PaymentStatus.PAID : DTOPaymentDetails.PaymentStatus.CREATED);
        newOrder.setPaymentDetails(paymentDetails);

        if (isInstant) {
            newOrder.setStatus(OrderStatus.PAID);
        }

        // 5. Salva e Retorna
        newOrder = orderEntityRepository.save(newOrder);
        return convertToResponse(newOrder);
    }

    @Override
    public OrderResponse getOrderById(String orderId) {
        OrderEntity order = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + orderId));
        return convertToResponse(order);
    }

    @Override
    public List<OrderResponse> getLatestOrders() {
        return orderEntityRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelOrder(String orderId) {
        OrderEntity order = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + orderId));

        if (!CANCELLATION_ALLOWED_STATUSES.contains(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Não é possível cancelar pedido com status: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);

        // Sincroniza o status do detalhe de pagamento também
        if(order.getPaymentDetails() != null) {
            order.getPaymentDetails().setStatus(DTOPaymentDetails.PaymentStatus.CANCELLED);
        }

        orderEntityRepository.save(order);
    }

    @Override
    public void deleteOrder(String orderId) {
        OrderEntity order = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        orderEntityRepository.delete(order);
    }

    @Override
    public OrderResponse updateOrderStatus(String orderId, DTOOrderStatusRequest request) {
        OrderEntity order = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        // Lógica simples de atualização
        if (request.getStatus() == OrderStatus.CANCELLED) {
            cancelOrder(orderId); // Reutiliza a lógica de cancelamento
            return convertToResponse(orderEntityRepository.findByOrderId(orderId).get());
        }

        order.setStatus(request.getStatus());
        return convertToResponse(orderEntityRepository.save(order));
    }

    // --- Conversor Auxiliar ---
    private OrderResponse convertToResponse(OrderEntity entity) {
        return OrderResponse.builder()
                .orderId(entity.getOrderId())
                .customerEmail(entity.getCustomer().getEmail())
                .customerName(entity.getCustomerName())
                .status(entity.getStatus())
                .grandTotal(entity.getGrandTotal())
                .paymentMethod(entity.getPaymentMethod())
                .createdAt(entity.getCreatedAt())
                .products(entity.getProducts().stream()
                        .map(p -> new OrderResponse.OrderProductResponse(p.getProductId(), p.getName(), p.getPrice(), p.getQuantity()))
                        .collect(Collectors.toList()))
                .build();
    }
}