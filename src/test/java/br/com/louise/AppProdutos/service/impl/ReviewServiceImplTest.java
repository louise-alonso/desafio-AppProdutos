package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.review.DTOReviewRequest;
import br.com.louise.AppProdutos.dto.review.DTOReviewResponse;
import br.com.louise.AppProdutos.model.*;
import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.repository.ReviewRepository;
import br.com.louise.AppProdutos.repository.UserRepository;
import br.com.louise.AppProdutos.service.impl.ReviewServiceImpl;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ReviewServiceImplTest {

    @InjectMocks
    private ReviewServiceImpl reviewService;

    @Mock private ReviewRepository reviewRepository;
    @Mock private OrderEntityRepository orderRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    private UserEntity user;
    private ProductEntity product;
    private OrderEntity order;

    @BeforeEach
    void setup() {
        // Mock de Segurança
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("cliente@teste.com");

        // Dados Básicos
        user = UserEntity.builder().email("cliente@teste.com").name("Cliente").build();
        product = ProductEntity.builder().productId("prod-1").name("TV").build();

        // Pedido Válido (Pago e do Usuário)
        order = OrderEntity.builder()
                .orderId("ord-1")
                .customer(user)
                .status(OrderStatus.PAID)
                .products(new ArrayList<>())
                .build();

        order.getProducts().add(OrderProductEntity.builder().productId("prod-1").build());
    }

    @Test
    void createReview_ShouldSucceed_AndRecalculateAverage() {
        // Arrange
        DTOReviewRequest request = new DTOReviewRequest();
        request.setProductId("prod-1");
        request.setOrderId("ord-1");
        request.setRating(5);
        request.setComment("Ótimo!");

        when(userRepository.findByEmail("cliente@teste.com")).thenReturn(Optional.of(user));
        when(productRepository.findByProductId("prod-1")).thenReturn(Optional.of(product));
        when(orderRepository.findByOrderId("ord-1")).thenReturn(Optional.of(order));
        when(reviewRepository.existsByOrderIdAndProductProductId("ord-1", "prod-1")).thenReturn(false);

        // Mock do salvamento do review
        ReviewEntity savedReview = ReviewEntity.builder().id(1L).rating(5).comment("Ótimo!").user(user).build();
        when(reviewRepository.save(any(ReviewEntity.class))).thenReturn(savedReview);

        // --- MOCK IMPORTANTE: LISTA DE REVIEWS PARA CALCULAR MÉDIA ---
        // Simulamos que agora existem 2 reviews: um nota 5 e outro nota 3 (Média esperada: 4.0)
        ReviewEntity r1 = ReviewEntity.builder().rating(5).build();
        ReviewEntity r2 = ReviewEntity.builder().rating(3).build();
        when(reviewRepository.findByProductProductIdOrderByCreatedAtDesc("prod-1"))
                .thenReturn(List.of(r1, r2));
        // ------------------------------------------------------------

        // Act
        DTOReviewResponse response = reviewService.createReview(request);

        // Assert
        assertNotNull(response);
        assertEquals(5, response.getRating());

        // Verifica se o produto foi salvo com a nova média
        verify(productRepository, times(1)).save(product);
        assertEquals(4.0, product.getAverageRating()); // (5+3)/2 = 4.0
        assertEquals(2, product.getReviewCount());
    }

    @Test
    void createReview_ShouldFail_WhenOrderNotBelongsToUser() {
        // Arrange
        // O usuário logado é "cliente@teste.com" (definido no setup)

        // Mas o pedido pertence a "outro@teste.com"
        UserEntity outroUser = UserEntity.builder().email("outro@teste.com").build();
        order.setCustomer(outroUser); // Altera o dono do pedido

        DTOReviewRequest request = new DTOReviewRequest();
        request.setProductId("prod-1");
        request.setOrderId("ord-1");
        request.setRating(5); // Obrigatório para não dar NullPointer

        // Mocks necessários para chegar até a validação do dono
        when(userRepository.findByEmail("cliente@teste.com")).thenReturn(Optional.of(user));
        when(productRepository.findByProductId("prod-1")).thenReturn(Optional.of(product));
        when(orderRepository.findByOrderId("ord-1")).thenReturn(Optional.of(order));

        // Act & Assert
        // Deve falhar porque user.email ("cliente") != order.customer.email ("outro")
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> reviewService.createReview(request));

        // Opcional: Verificar a mensagem
        // assertEquals("Este pedido não pertence a você.", exception.getReason());
    }

    @Test
    void createReview_ShouldFail_WhenOrderIsNotPaid() {
        // Arrange
        order.setStatus(OrderStatus.CREATED); // Pedido não pago

        DTOReviewRequest request = new DTOReviewRequest();
        request.setProductId("prod-1");
        request.setOrderId("ord-1");
        request.setRating(5); // Obrigatório

        // Mocks para passar pelas validações anteriores (User, Product, Order exists)
        when(userRepository.findByEmail("cliente@teste.com")).thenReturn(Optional.of(user));
        when(productRepository.findByProductId("prod-1")).thenReturn(Optional.of(product));
        when(orderRepository.findByOrderId("ord-1")).thenReturn(Optional.of(order));

        // Act & Assert
        assertThrows(ResponseStatusException.class,
                () -> reviewService.createReview(request));
    }
}