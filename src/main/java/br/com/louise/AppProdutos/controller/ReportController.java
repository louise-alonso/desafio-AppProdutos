package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.report.DTOSalesReport;
import br.com.louise.AppProdutos.dto.report.DTOTopProduct;
import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "09. Relatórios e Auditoria", description = "Métricas administrativas")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Relatório de Vendas", description = "Retorna o volume de vendas e faturamento agrupado por dia. Filtro opcional por data (Default: últimos 30 dias).")
    public List<DTOSalesReport> getSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        if (start == null) start = LocalDate.now().minusDays(30);
        if (end == null) end = LocalDate.now();

        return reportService.getSalesReport(start, end);
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Produtos mais vendidos", description = "Ranking dos 10 produtos com maior volume de vendas.")
    public List<DTOTopProduct> getTopProducts() {
        return reportService.getTopSellingProducts();
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Alerta de stock baixo", description = "Lista produtos com quantidade abaixo do limite de segurança (Default: 5 unidades).")
    public List<ProductEntity> getLowStock(@RequestParam(defaultValue = "5") Integer min) {
        return reportService.getLowStockProducts(min);
    }
}