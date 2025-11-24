package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.order.DTOOrderRequest;
import br.com.louise.AppProdutos.dto.order.DTOOrderResponse;
import br.com.louise.AppProdutos.dto.payment.PaymentMethod;
import br.com.louise.AppProdutos.model.*;
import br.com.louise.AppProdutos.repository.CartRepository;
import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import br.com.louise.AppProdutos.repository.UserRepository;
import br.com.louise.AppProdutos.service.CartService;
import br.com.louise.AppProdutos.service.InventoryService;
import br.com.louise.AppProdutos.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock private OrderEntityRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private CartRepository cartRepository;
    @Mock private CartService cartService;
    @Mock private InventoryService inventoryService;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    private UserEntity user;
    private CartEntity cart;
    private ProductEntity product;

    @BeforeEach
    void setup() {
        // Mock do Usuário Logado
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("cliente@teste.com");

        user = UserEntity.builder().email("cliente@teste.com").name("Cliente").build();
        product = ProductEntity.builder().productId("prod-1").price(new BigDecimal("100.00")).name("TV").build();

        cart = CartEntity.builder().user(user).items(new ArrayList<>()).build();
        CartItemEntity item = CartItemEntity.builder().product(product).quantity(2).cart(cart).build();
        cart.getItems().add(item);
    }

    @Test
    void createOrder_ShouldSucceed_WhenCartIsValid() {
        // Arrange
        DTOOrderRequest request = br.com.louise.AppProdutos.dto.order.DTOOrderRequest.builder().paymentMethod(PaymentMethod.BOLETO).build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmail(anyString())).thenReturn(Optional.of(cart));
        when(orderRepository.save(any(OrderEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        DTOOrderResponse response = orderService.createOrder(request);

        // Assert
        assertNotNull(response);
        assertEquals(OrderStatus.CREATED, response.getStatus());


        assertEquals(0, new BigDecimal("220.00").compareTo(response.getGrandTotal()));
        // ---------------------

        // Verify (Garanta que o sistema chamou os outros serviços)
        verify(inventoryService, times(1)).processTransaction(any(), anyString());
        verify(cartService, times(1)).clearCart();
    }

    @Test
    void createOrder_ShouldFail_WhenCartIsEmpty() {
        cart.getItems().clear(); // Carrinho vazio
        DTOOrderRequest request = br.com.louise.AppProdutos.dto.order.DTOOrderRequest.builder().build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(cartRepository.findByUserEmail(anyString())).thenReturn(Optional.of(cart));

        assertThrows(Exception.class, () -> orderService.createOrder(request));
        verify(orderRepository, never()).save(any()); // Garante que NADA foi salvo
    }
}