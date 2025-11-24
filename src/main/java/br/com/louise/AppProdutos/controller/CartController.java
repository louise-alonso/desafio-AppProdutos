package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.cart.DTOCartItemRequest;
import br.com.louise.AppProdutos.dto.cart.DTOCartResponse;
import br.com.louise.AppProdutos.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
@Tag(name = "06. Carrinho de Compras", description = "Gestão de itens antes da compra")
public class CartController {

    private final CartService cartService;

    @GetMapping
    @Operation(summary = "Ver carrinho atual", description = "Recupera o carrinho ativo do utilizador logado, com todos os itens e totais calculados.")
    public DTOCartResponse getCart() {
        return cartService.getMyCart();
    }

    @PostMapping("/add")
    @Operation(summary = "Adicionar item", description = "Adiciona um produto ao carrinho. Se o produto já existir, incrementa a quantidade. Valida se há stock suficiente.")
    public DTOCartResponse addItem(@RequestBody DTOCartItemRequest request) {
        return cartService.addItemToCart(request);
    }

    @DeleteMapping("/remove/{productId}")
    @Operation(summary = "Remover item", description = "Remove um produto específico do carrinho, independentemente da quantidade.")
    public DTOCartResponse removeItem(@PathVariable String productId) {
        return cartService.removeItem(productId);
    }

    @DeleteMapping("/clear")
    @Operation(summary = "Esvaziar carrinho", description = "Remove todos os itens do carrinho de uma só vez.")
    public void clearCart() {
        cartService.clearCart();
    }
}