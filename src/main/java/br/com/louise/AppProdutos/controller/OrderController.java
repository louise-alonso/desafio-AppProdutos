package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.order.DTOOrderRequest;
import br.com.louise.AppProdutos.dto.order.DTOOrderResponse;
import br.com.louise.AppProdutos.dto.order.DTOOrderStatusRequest;

import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import br.com.louise.AppProdutos.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "07. Pedidos e Pagamentos", description = "Fluxo de checkout e gestão de vendas")
public class OrderController {

    private final OrderService orderService;
    private final OrderEntityRepository orderRepository;

    // Método auxiliar (mantido, mas não é endpoint)
    public boolean isOrderOwner(String orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        return orderRepository.findByOrderId(orderId)
                .map(order -> order.getCustomer().getEmail().equals(currentEmail))
                .orElse(false);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @Operation(summary = "Checkout (Criar Pedido)", description = "Transforma o carrinho atual do utilizador num pedido fechado. Valida stock, aplica cupão e regista a venda.")
    public DTOOrderResponse createOrder(@RequestBody DTOOrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or @orderPermissionService.isOrderOwner(#orderId)")
    @Operation(summary = "Consultar Pedido", description = "Retorna os detalhes de um pedido (itens, total, status). Apenas o dono do pedido ou ADMIN têm acesso.")
    public DTOOrderResponse getOrderById(@PathVariable String orderId) {
        try {
            return orderService.getOrderById(orderId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado");
        }
    }

    @GetMapping("/latest")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Últimos Pedidos", description = "Lista os pedidos mais recentes da loja. Exclusivo para ADMIN.")
    public List<DTOOrderResponse> getLatestOrders() {
        return orderService.getLatestOrders();
    }

    @PostMapping("/{orderId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @orderPermissionService.isOrderOwner(#orderId)")
    @Operation(summary = "Cancelar Pedido", description = "Cancela um pedido e estorna o stock automaticamente. Regra: Só pode cancelar se o status for CREATED ou PAID.")
    public void cancelOrder(@PathVariable String orderId) {
        try {
            orderService.cancelOrder(orderId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar Status (Logística)", description = "Permite ao ADMIN avançar o status do pedido (ex: de PAID para SHIPPED/DELIVERED).")
    public DTOOrderResponse updateOrderStatus(@PathVariable String orderId, @RequestBody DTOOrderStatusRequest request) {
        try {
            return orderService.updateOrderStatus(orderId, request);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado.");
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}