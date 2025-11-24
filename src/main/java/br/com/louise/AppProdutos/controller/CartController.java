package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.cart.DTOCartItemRequest;
import br.com.louise.AppProdutos.dto.cart.DTOCartResponse;
import br.com.louise.AppProdutos.service.CartService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "06. Carrinho de Compras")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public DTOCartResponse getCart() {
        return cartService.getMyCart();
    }

    @PostMapping("/add")
    public DTOCartResponse addItem(@RequestBody DTOCartItemRequest request) {
        return cartService.addItemToCart(request);
    }

    @DeleteMapping("/remove/{productId}")
    public DTOCartResponse removeItem(@PathVariable String productId) {
        return cartService.removeItem(productId);
    }

    @DeleteMapping("/clear")
    public void clearCart() {
        cartService.clearCart();
    }
}