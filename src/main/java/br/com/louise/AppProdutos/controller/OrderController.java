package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.order.DTOOrderRequest;
import br.com.louise.AppProdutos.dto.order.DTOOrderResponse;
import br.com.louise.AppProdutos.dto.order.DTOOrderStatusRequest;

import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import br.com.louise.AppProdutos.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final OrderEntityRepository orderRepository;

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
    public DTOOrderResponse createOrder(@RequestBody DTOOrderRequest orderRequest) {
        return orderService.createOrder(orderRequest);
    }

    @GetMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN') or @orderPermissionService.isOrderOwner(#orderId)")
    public DTOOrderResponse getOrderById(@PathVariable String orderId) {
        try {
            return orderService.getOrderById(orderId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado");
        }
    }

    @GetMapping("/latest")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DTOOrderResponse> getLatestOrders() {
        return orderService.getLatestOrders();
    }

    @PostMapping("/{orderId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN') or @orderPermissionService.isOrderOwner(#orderId)")
    public void cancelOrder(@PathVariable String orderId) {
        try {
            orderService.cancelOrder(orderId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
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