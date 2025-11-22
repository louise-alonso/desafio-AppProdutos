package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.DTOOrderStatusRequest;
import br.com.louise.AppProdutos.dto.OrderRequest;
import br.com.louise.AppProdutos.dto.OrderResponse;
import br.com.louise.AppProdutos.service.OrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor; // Importação necessária
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor // Adicionado para injeção do OrderService
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        // CORRIGIDO: Retorna o resultado e usa a variável correta (orderRequest)
        return orderService.createOrder(orderRequest);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{orderId}")
    public void deleteOrder(@PathVariable String orderId) {
        orderService.deleteOrder(orderId);
    }

    @GetMapping("/latest")
    // CORRIGIDO: Tipo de retorno para List<OrderResponse> e sintaxe do método
    public List<OrderResponse> getLatestOrders() {
        return orderService.getLatestOrders();
    }

    @PutMapping("/{orderId}/status")
    public OrderResponse updateOrderStatus(@PathVariable String orderId, @RequestBody DTOOrderStatusRequest request) {
                try {
                    // A permissão aqui deve ser checada: @PreAuthorize("hasRole('ROLE_ADMIN')")
                    return orderService.updateOrderStatus(orderId, request);
                } catch (EntityNotFoundException e) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado.");
                } catch (RuntimeException e) {
                    // Captura a exceção de regra de negócio do Service
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
                }
}
}