package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.inventory.DTOInventoryRequest;
import br.com.louise.AppProdutos.dto.order.DTOOrderRequest;
import br.com.louise.AppProdutos.dto.order.DTOOrderResponse;
import br.com.louise.AppProdutos.dto.order.DTOOrderStatusRequest;
import br.com.louise.AppProdutos.dto.payment.DTOPaymentDetails;
import br.com.louise.AppProdutos.dto.payment.PaymentMethod;
import br.com.louise.AppProdutos.model.*;
import br.com.louise.AppProdutos.repository.CartRepository;
import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.repository.UserRepository;
import br.com.louise.AppProdutos.service.CartService;
import br.com.louise.AppProdutos.service.CouponService;
import br.com.louise.AppProdutos.service.InventoryService;
import br.com.louise.AppProdutos.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;
    private final CartRepository cartRepository;
    private final CartService cartService;
    private final CouponService couponService;

    private static final List<OrderStatus> CANCELLATION_ALLOWED_STATUSES = Arrays.asList(
            OrderStatus.CREATED,
            OrderStatus.PAID
    );

    @Override
    @Transactional
    public DTOOrderResponse createOrder(DTOOrderRequest request) {
        // 1. Identifica o Cliente Logado
        String customerEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Cliente logado não encontrado."));

        // 2. Busca o Carrinho do Cliente
        CartEntity cart = cartRepository.findByUserEmail(customerEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Carrinho vazio ou inexistente."));

        if (cart.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Carrinho vazio.");
        }

        // 3. Converte Itens e Baixa Estoque
        List<OrderProductEntity> orderProducts = cart.getItems().stream()
                .map(cartItem -> {
                    ProductEntity product = cartItem.getProduct();

                    // Baixa no Estoque
                    DTOInventoryRequest inventoryReq = new DTOInventoryRequest();
                    inventoryReq.setProductId(product.getProductId());
                    inventoryReq.setQuantity(cartItem.getQuantity());
                    inventoryReq.setType(TransactionType.EXIT);
                    inventoryReq.setDescription("Venda (Checkout)");

                    inventoryService.processTransaction(inventoryReq, customerEmail);

                    return OrderProductEntity.builder()
                            .productId(product.getProductId())
                            .name(product.getName())
                            .price(product.getPrice())
                            .quantity(cartItem.getQuantity())
                            .build();
                })
                .collect(Collectors.toList());

        // 4. Calcula Subtotal
        BigDecimal subTotal = orderProducts.stream()
                .map(p -> p.getPrice().multiply(new BigDecimal(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // --- LÓGICA DE CUPOM (CORRIGIDA) ---
        BigDecimal discount = BigDecimal.ZERO;
        String couponCodeUsed = null;

        if (request.getCouponCode() != null && !request.getCouponCode().isEmpty()) {
            // AQUI ESTAVA O ERRO: Agora usamos o método inteligente da interface
            discount = couponService.validateAndCalculateDiscount(
                    request.getCouponCode(),
                    customer,
                    orderProducts,
                    subTotal
            );

            couponCodeUsed = request.getCouponCode();

            // Incrementa o uso do cupom
            couponService.incrementGlobalUsage(couponCodeUsed);
        }
        // -----------------------------------

        // Calcula Taxa e GrandTotal
        BigDecimal tax = subTotal.multiply(new BigDecimal("0.10"));

        // Total = Subtotal - Desconto + Taxa
        BigDecimal grandTotal = subTotal.subtract(discount).add(tax);

        if (grandTotal.compareTo(BigDecimal.ZERO) < 0) {
            grandTotal = BigDecimal.ZERO;
        }

        // 5. Monta Pedido
        OrderEntity newOrder = OrderEntity.builder()
                .customer(customer)
                .customerName(request.getCustomerName() != null ? request.getCustomerName() : customer.getName())
                .phoneNumber(request.getPhoneNumber())
                .status(OrderStatus.CREATED)
                .paymentMethod(request.getPaymentMethod())
                .tax(tax)
                .discount(discount) // Salva o valor do desconto
                .appliedCoupon(couponCodeUsed) // Salva o código usado
                .grandTotal(grandTotal)
                .products(orderProducts)
                .build();

        DTOPaymentDetails paymentDetails = new DTOPaymentDetails();
        boolean isInstant = request.getPaymentMethod() == PaymentMethod.PIX || request.getPaymentMethod() == PaymentMethod.DINHEIRO;
        paymentDetails.setStatus(isInstant ? DTOPaymentDetails.PaymentStatus.PAID : DTOPaymentDetails.PaymentStatus.CREATED);
        newOrder.setPaymentDetails(paymentDetails);

        if (isInstant) {
            newOrder.setStatus(OrderStatus.PAID);
        }

        // 6. Salva e Limpa Carrinho
        newOrder = orderEntityRepository.save(newOrder);
        cartService.clearCart();

        return convertToResponse(newOrder);
    }

    // --- Outros Métodos (Cancel, Delete, Get) mantêm-se iguais ---

    @Override
    @Transactional
    public void cancelOrder(String orderId) {
        OrderEntity order = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + orderId));

        if (!CANCELLATION_ALLOWED_STATUSES.contains(order.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cancelamento não permitido para status: " + order.getStatus());
        }

        // Devolve Estoque
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        for (OrderProductEntity item : order.getProducts()) {
            DTOInventoryRequest inventoryReq = new DTOInventoryRequest();
            inventoryReq.setProductId(item.getProductId());
            inventoryReq.setQuantity(item.getQuantity());
            inventoryReq.setType(TransactionType.RETURN);
            inventoryReq.setDescription("Cancelamento Pedido #" + order.getOrderId());
            inventoryService.processTransaction(inventoryReq, userEmail);
        }

        order.setStatus(OrderStatus.CANCELLED);
        if(order.getPaymentDetails() != null) {
            order.getPaymentDetails().setStatus(DTOPaymentDetails.PaymentStatus.CANCELLED);
        }
        orderEntityRepository.save(order);
    }

    @Override
    public DTOOrderResponse getOrderById(String orderId) {
        OrderEntity order = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado: " + orderId));
        return convertToResponse(order);
    }

    @Override
    public List<DTOOrderResponse> getLatestOrders() {
        return orderEntityRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteOrder(String orderId) {
        OrderEntity order = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));
        orderEntityRepository.delete(order);
    }

    @Override
    public DTOOrderResponse updateOrderStatus(String orderId, DTOOrderStatusRequest request) {
        OrderEntity order = orderEntityRepository.findByOrderId(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        if (request.getStatus() == OrderStatus.CANCELLED) {
            cancelOrder(orderId);
            return convertToResponse(orderEntityRepository.findByOrderId(orderId).get());
        }

        order.setStatus(request.getStatus());
        return convertToResponse(orderEntityRepository.save(order));
    }

    private DTOOrderResponse convertToResponse(OrderEntity entity) {
        return DTOOrderResponse.builder()
                .orderId(entity.getOrderId())
                .customerEmail(entity.getCustomer().getEmail())
                .customerName(entity.getCustomerName())
                .status(entity.getStatus())
                .grandTotal(entity.getGrandTotal())
                .paymentMethod(entity.getPaymentMethod())
                .createdAt(entity.getCreatedAt())
                .products(entity.getProducts().stream()
                        .map(p -> new DTOOrderResponse.OrderProductResponse(p.getProductId(), p.getName(), p.getPrice(), p.getQuantity()))
                        .collect(Collectors.toList()))
                .build();
    }
}