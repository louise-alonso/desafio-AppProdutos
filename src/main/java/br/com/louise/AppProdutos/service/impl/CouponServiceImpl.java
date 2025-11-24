package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.coupon.DTOCouponRequest;
import br.com.louise.AppProdutos.model.*;
import br.com.louise.AppProdutos.repository.CouponRepository;
import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import br.com.louise.AppProdutos.service.CouponService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final OrderEntityRepository orderRepository;

    @Override
    public CouponEntity createCoupon(DTOCouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cupom já existe");
        }

        CouponEntity coupon = CouponEntity.builder()
                .code(request.getCode().toUpperCase())
                .type(request.getType())
                .value(request.getValue())
                .expirationDate(request.getExpirationDate())
                .globalUsageLimit(request.getGlobalUsageLimit())
                .usageLimitPerUser(request.getUsageLimitPerUser())
                .minOrderValue(request.getMinOrderValue())
                .targetCategoryId(request.getTargetCategoryId())
                .targetProductId(request.getTargetProductId())
                .active(true)
                .currentUsageCount(0)
                .build();

        return couponRepository.save(coupon);
    }

    @Override
    public BigDecimal validateAndCalculateDiscount(String code, UserEntity user, List<OrderProductEntity> products, BigDecimal orderSubTotal) {

        // 1. Busca
        CouponEntity coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Cupom não encontrado: " + code));

        // 2. Validações Básicas
        if (!coupon.getActive() || coupon.getExpirationDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cupom expirado ou inativo.");
        }

        // 3. Valida Limite Global
        if (coupon.getGlobalUsageLimit() != null && coupon.getCurrentUsageCount() >= coupon.getGlobalUsageLimit()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este cupom esgotou seu limite de uso global.");
        }

        // 4. Valida Valor Mínimo
        if (coupon.getMinOrderValue() != null && orderSubTotal.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Valor mínimo para este cupom: " + coupon.getMinOrderValue());
        }

        // 5. Valida Limite por Usuário
        if (coupon.getUsageLimitPerUser() != null) {
            Long usageCount = orderRepository.countByCustomerAndAppliedCoupon(user, coupon.getCode());
            if (usageCount >= coupon.getUsageLimitPerUser()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você já atingiu o limite de uso deste cupom.");
            }
        }

        // 6. Lógica de Alvo (Produto Específico)
        BigDecimal applicableTotal = orderSubTotal;

        if (coupon.getTargetProductId() != null) {
            applicableTotal = products.stream()
                    .filter(p -> p.getProductId().equals(coupon.getTargetProductId()))
                    .map(p -> p.getPrice().multiply(new BigDecimal(p.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            if (applicableTotal.compareTo(BigDecimal.ZERO) == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este cupom é exclusivo para o produto " + coupon.getTargetProductId());
            }
        }

        // 7. Cálculo Final
        BigDecimal discountValue;
        if (coupon.getType() == DiscountType.PERCENTAGE) {
            BigDecimal percentage = coupon.getValue().divide(new BigDecimal("100"));
            discountValue = applicableTotal.multiply(percentage);
        } else {
            discountValue = coupon.getValue();
        }

        // Trava de segurança (Desconto não pode ser maior que o subtotal)
        if (discountValue.compareTo(orderSubTotal) > 0) {
            return orderSubTotal;
        }

        return discountValue;
    }

    @Override
    public void incrementGlobalUsage(String code) {
        couponRepository.findByCode(code).ifPresent(coupon -> {
            coupon.setCurrentUsageCount(coupon.getCurrentUsageCount() + 1);
            couponRepository.save(coupon);
        });
    }
}