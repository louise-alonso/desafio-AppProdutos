package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.dto.cart.DTOCartItemRequest;
import br.com.louise.AppProdutos.dto.cart.DTOCartResponse;

public interface CartService {
    DTOCartResponse getMyCart();
    DTOCartResponse addItemToCart(DTOCartItemRequest request);
    DTOCartResponse removeItem(String productId);
    void clearCart();
}