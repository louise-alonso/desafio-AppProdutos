package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.report.DTOSalesReport;
import br.com.louise.AppProdutos.dto.report.DTOTopProduct;
import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DTOSalesReport> getSalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end
    ) {
        // Default: Últimos 30 dias se não informar data
        if (start == null) start = LocalDate.now().minusDays(30);
        if (end == null) end = LocalDate.now();

        return reportService.getSalesReport(start, end);
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasRole('ADMIN')")
    public List<DTOTopProduct> getTopProducts() {
        return reportService.getTopSellingProducts();
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProductEntity> getLowStock(@RequestParam(defaultValue = "5") Integer min) {
        return reportService.getLowStockProducts(min);
    }
}