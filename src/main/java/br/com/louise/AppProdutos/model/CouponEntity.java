package br.com.louise.AppProdutos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tbl_coupons")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String code;

    // --- CORREÇÃO AQUI: Mapeando para os novos nomes no banco ---
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false)
    private DiscountType type;

    @Column(name = "discount_value", nullable = false)
    private BigDecimal value;
    // ------------------------------------------------------------

    @Column(nullable = false)
    private LocalDate expirationDate;

    private Boolean active;

    private Integer globalUsageLimit;

    @Builder.Default
    private Integer currentUsageCount = 0;

    private Integer usageLimitPerUser;

    private BigDecimal minOrderValue;

    private String targetCategoryId;

    private String targetProductId;
}