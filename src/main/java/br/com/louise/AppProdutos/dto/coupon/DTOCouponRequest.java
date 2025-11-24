package br.com.louise.AppProdutos.dto;

import br.com.louise.AppProdutos.model.DiscountType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DTOCouponRequest {
    private String code;
    private DiscountType type;
    private BigDecimal value;
    private LocalDate expirationDate;

    private Integer globalUsageLimit;
    private Integer usageLimitPerUser;
    private BigDecimal minOrderValue;
    private String targetCategoryId;
    private String targetProductId;
}