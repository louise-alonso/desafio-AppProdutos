package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.dto.report.DTOSalesReport;
import br.com.louise.AppProdutos.dto.report.DTOTopProduct;
import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import br.com.louise.AppProdutos.repository.OrderProductEntityRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderEntityRepository orderRepository;
    private final OrderProductEntityRepository orderItemRepository;
    private final ProductRepository productRepository;

    public List<DTOSalesReport> getSalesReport(LocalDate start, LocalDate end) {
        // Ajusta horário para pegar o dia inteiro (00:00:00 até 23:59:59)
        return orderRepository.getSalesReport(start.atStartOfDay(), end.atTime(23, 59, 59));
    }

    public List<DTOTopProduct> getTopSellingProducts() {
        // Retorna o Top 10
        return orderItemRepository.findTopSellingProducts().stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<ProductEntity> getLowStockProducts(Integer threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }
}