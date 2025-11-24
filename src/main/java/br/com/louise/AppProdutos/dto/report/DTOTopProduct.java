package br.com.louise.AppProdutos.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTOTopProduct {
    private String productName;
    private Long totalQuantitySold;
    private BigDecimal totalRevenue;
}