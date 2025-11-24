package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.report.DTOSalesReport;
import br.com.louise.AppProdutos.dto.report.DTOTopProduct;
import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import br.com.louise.AppProdutos.repository.OrderProductEntityRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderEntityRepository orderRepository;
    private final OrderProductEntityRepository orderItemRepository;
    private final ProductRepository productRepository;

    @Override
    public List<DTOSalesReport> getSalesReport(LocalDate start, LocalDate end) {
        return orderRepository.getSalesReport(start.atStartOfDay(), end.atTime(23, 59, 59));
    }

    @Override
    public List<DTOTopProduct> getTopSellingProducts() {
        return orderItemRepository.findTopSellingProducts().stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductEntity> getLowStockProducts(Integer threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }
}