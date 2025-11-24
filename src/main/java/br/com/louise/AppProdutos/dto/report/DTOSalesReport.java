package br.com.louise.AppProdutos.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTOSalesReport {
    private LocalDate date;
    private Long totalOrders;
    private BigDecimal totalRevenue;
}