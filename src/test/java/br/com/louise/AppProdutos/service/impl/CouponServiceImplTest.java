package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.model.CouponEntity;
import br.com.louise.AppProdutos.model.DiscountType;
import br.com.louise.AppProdutos.model.UserEntity;
import br.com.louise.AppProdutos.repository.CouponRepository;
import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import br.com.louise.AppProdutos.service.impl.CouponServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceImplTest {

    @InjectMocks
    private CouponServiceImpl couponService;

    @Mock private CouponRepository couponRepository;
    @Mock private OrderEntityRepository orderRepository;

    private CouponEntity validCoupon;
    private UserEntity user;

    @BeforeEach
    void setup() {
        user = UserEntity.builder().userId("user-1").build();

        validCoupon = CouponEntity.builder()
                .code("DESCONTO10")
                .type(DiscountType.PERCENTAGE)
                .value(new BigDecimal("10.00")) // 10%
                .expirationDate(LocalDate.now().plusDays(5)) // Vence no futuro
                .active(true)
                .usageLimitPerUser(1) // Limite de 1 por pessoa
                .minOrderValue(new BigDecimal("50.00")) // Mínimo R$ 50
                .build();
    }

    @Test
    void validate_ShouldCalculatePercentageDiscount_WhenValid() {
        // Arrange: Pedido de R$ 200,00
        BigDecimal orderTotal = new BigDecimal("200.00");
        when(couponRepository.findByCode("DESCONTO10")).thenReturn(Optional.of(validCoupon));
        // Usuário nunca usou o cupom (count = 0)
        when(orderRepository.countByCustomerAndAppliedCoupon(user, "DESCONTO10")).thenReturn(0L);

        // Act
        BigDecimal discount = couponService.validateAndCalculateDiscount("DESCONTO10", user, Collections.emptyList(), orderTotal);

        // Assert: 10% de 200 = 20
        assertEquals(0, new BigDecimal("20.00").compareTo(discount));
    }

    @Test
    void validate_ShouldCalculateFixedDiscount_WhenValid() {
        // Arrange: Muda cupom para FIXO de R$ 15
        validCoupon.setType(DiscountType.FIXED);
        validCoupon.setValue(new BigDecimal("15.00"));

        BigDecimal orderTotal = new BigDecimal("100.00");
        when(couponRepository.findByCode("DESCONTO10")).thenReturn(Optional.of(validCoupon));
        when(orderRepository.countByCustomerAndAppliedCoupon(user, "DESCONTO10")).thenReturn(0L);

        // Act
        BigDecimal discount = couponService.validateAndCalculateDiscount("DESCONTO10", user, Collections.emptyList(), orderTotal);

        // Assert: Desconto fixo é R$ 15
        assertEquals(0, new BigDecimal("15.00").compareTo(discount));
    }

    @Test
    void validate_ShouldThrowError_WhenExpired() {
        validCoupon.setExpirationDate(LocalDate.now().minusDays(1)); // Venceu ontem
        when(couponRepository.findByCode("DESCONTO10")).thenReturn(Optional.of(validCoupon));

        assertThrows(ResponseStatusException.class, () -> {
            couponService.validateAndCalculateDiscount("DESCONTO10", user, Collections.emptyList(), new BigDecimal("100"));
        });
    }

    @Test
    void validate_ShouldThrowError_WhenUsageLimitReached() {
        // Arrange: Usuário já usou 1 vez (limite é 1)
        when(couponRepository.findByCode("DESCONTO10")).thenReturn(Optional.of(validCoupon));
        when(orderRepository.countByCustomerAndAppliedCoupon(user, "DESCONTO10")).thenReturn(1L);

        assertThrows(ResponseStatusException.class, () -> {
            couponService.validateAndCalculateDiscount("DESCONTO10", user, Collections.emptyList(), new BigDecimal("100"));
        });
    }

    @Test
    void validate_ShouldThrowError_WhenOrderValueIsTooLow() {
        // Arrange: Pedido de R$ 20 (Mínimo é 50)
        BigDecimal orderTotal = new BigDecimal("20.00");
        when(couponRepository.findByCode("DESCONTO10")).thenReturn(Optional.of(validCoupon));

        // --- LINHA REMOVIDA AQUI ---
        // O erro de valor mínimo acontece ANTES de checar o histórico do usuário.

        assertThrows(ResponseStatusException.class, () -> {
            couponService.validateAndCalculateDiscount("DESCONTO10", user, Collections.emptyList(), orderTotal);
        });
    }
}