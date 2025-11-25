package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.cart.DTOCartItemRequest;
import br.com.louise.AppProdutos.model.*;
import br.com.louise.AppProdutos.repository.CartRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.repository.UserRepository;
import br.com.louise.AppProdutos.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock private CartRepository cartRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    private ProductEntity product;
    private CartEntity cart;

    @BeforeEach
    void setup() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("user@test.com");

        product = ProductEntity.builder().productId("prod-1").stockQuantity(10).price(BigDecimal.TEN).build();
        cart = CartEntity.builder().items(new ArrayList<>()).build();
    }

    @Test
    void addItem_ShouldAddNewItem_WhenNotInCart() {
        DTOCartItemRequest req = new DTOCartItemRequest();
        req.setProductId("prod-1");
        req.setQuantity(2);

        when(cartRepository.findByUserEmail(any())).thenReturn(Optional.of(cart));
        when(productRepository.findByProductId("prod-1")).thenReturn(Optional.of(product));
        when(cartRepository.save(any())).thenReturn(cart);

        cartService.addItemToCart(req);

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getItems().get(0).getQuantity());
    }

    @Test
    void addItem_ShouldSumQuantity_WhenAlreadyInCart() {
        // Item já existe com qtd 1
        CartItemEntity existingItem = CartItemEntity.builder().product(product).quantity(1).build();
        cart.getItems().add(existingItem);

        DTOCartItemRequest req = new DTOCartItemRequest();
        req.setProductId("prod-1");
        req.setQuantity(2); // Adiciona mais 2

        when(cartRepository.findByUserEmail(any())).thenReturn(Optional.of(cart));
        when(productRepository.findByProductId("prod-1")).thenReturn(Optional.of(product));
        when(cartRepository.save(any())).thenReturn(cart);

        cartService.addItemToCart(req);

        assertEquals(1, cart.getItems().size()); // Continua sendo 1 item (linha)
        assertEquals(3, cart.getItems().get(0).getQuantity()); // 1 + 2 = 3
    }

    @Test
    void addItem_ShouldFail_WhenStockIsInsufficient() {
        product.setStockQuantity(1); // Estoque baixo

        DTOCartItemRequest req = new DTOCartItemRequest();
        req.setProductId("prod-1");
        req.setQuantity(5); // Tenta comprar 5

        when(cartRepository.findByUserEmail(any())).thenReturn(Optional.of(cart));
        when(productRepository.findByProductId("prod-1")).thenReturn(Optional.of(product));

        assertThrows(ResponseStatusException.class, () -> cartService.addItemToCart(req));
    }
}