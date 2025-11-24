package br.com.louise.AppProdutos.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DTOTopProduct {

    @Schema(description = "Nome do Produto", example = "Iphone 15")
    private String productName;

    @Schema(description = "Total de unidades vendidas", example = "120")
    private Long totalQuantitySold;

    @Schema(description = "Receita gerada por este produto", example = "950000.00")
    private BigDecimal totalRevenue;
}