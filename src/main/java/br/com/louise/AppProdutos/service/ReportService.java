package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.dto.report.DTOSalesReport;
import br.com.louise.AppProdutos.dto.report.DTOTopProduct;
import br.com.louise.AppProdutos.model.ProductEntity;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    List<DTOSalesReport> getSalesReport(LocalDate start, LocalDate end);
    List<DTOTopProduct> getTopSellingProducts();
    List<ProductEntity> getLowStockProducts(Integer threshold);
}