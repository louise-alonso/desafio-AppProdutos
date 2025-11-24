package br.com.louise.AppProdutos.dto.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class DTOSalesReport {

    @Schema(description = "Data da venda", example = "2023-11-24")
    private LocalDate date;

    @Schema(description = "Quantidade de pedidos no dia", example = "15")
    private Long quantityOfOrders;

    @Schema(description = "Faturamento total do dia", example = "4500.50")
    private BigDecimal totalRevenue;
}