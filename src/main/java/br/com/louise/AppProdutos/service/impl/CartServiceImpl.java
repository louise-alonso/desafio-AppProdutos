package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.cart.DTOCartItemRequest;
import br.com.louise.AppProdutos.dto.cart.DTOCartResponse;
import br.com.louise.AppProdutos.model.CartEntity;
import br.com.louise.AppProdutos.model.CartItemEntity;
import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.model.UserEntity;
import br.com.louise.AppProdutos.repository.CartRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.repository.UserRepository;
import br.com.louise.AppProdutos.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public DTOCartResponse getMyCart() {
        CartEntity cart = getOrCreateCart();
        return convertToResponse(cart);
    }

    @Override
    @Transactional
    public DTOCartResponse addItemToCart(DTOCartItemRequest request) {
        CartEntity cart = getOrCreateCart();
        ProductEntity product = productRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        // Busca se o item já existe no carrinho
        Optional<CartItemEntity> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getProductId().equals(product.getProductId()))
                .findFirst();

        int currentQuantityInCart = existingItem.map(CartItemEntity::getQuantity).orElse(0);
        int totalDesired = currentQuantityInCart + request.getQuantity();

        if (product.getStockQuantity() < totalDesired) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Estoque insuficiente. Disponível: " + product.getStockQuantity() + ", Você já tem: " + currentQuantityInCart);
        }

        if (existingItem.isPresent()) {
            CartItemEntity item = existingItem.get();
            item.setQuantity(totalDesired);
        } else {
            CartItemEntity newItem = CartItemEntity.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        cart = cartRepository.save(cart);
        return convertToResponse(cart);
    }

    @Override
    @Transactional
    public DTOCartResponse removeItem(String productId) {
        CartEntity cart = getOrCreateCart();

        boolean removed = cart.getItems().removeIf(item -> item.getProduct().getProductId().equals(productId));

        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não estava no carrinho");
        }

        cart = cartRepository.save(cart);
        return convertToResponse(cart);
    }

    @Override
    @Transactional
    public void clearCart() {
        CartEntity cart = getOrCreateCart();
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    // --- Métodos Auxiliares ---

    private CartEntity getOrCreateCart() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return cartRepository.findByUserEmail(email)
                .orElseGet(() -> {
                    UserEntity user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

                    CartEntity newCart = CartEntity.builder().user(user).build();
                    return cartRepository.save(newCart);
                });
    }

    private DTOCartResponse convertToResponse(CartEntity cart) {
        return DTOCartResponse.builder()
                .cartId(cart.getId())
                .totalAmount(cart.getTotalAmount())
                .items(cart.getItems().stream().map(this::convertItem).collect(Collectors.toList()))
                .build();
    }

    private DTOCartResponse.DTOCartItemResponse convertItem(CartItemEntity item) {
        return DTOCartResponse.DTOCartItemResponse.builder()
                .itemId(item.getId())
                .productId(item.getProduct().getProductId())
                .productName(item.getProduct().getName())
                .unitPrice(item.getProduct().getPrice())
                .quantity(item.getQuantity())
                .subTotal(item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .build();
    }
}