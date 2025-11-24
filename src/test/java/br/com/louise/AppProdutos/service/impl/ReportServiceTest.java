package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.report.DTOSalesReport;
import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import br.com.louise.AppProdutos.repository.OrderProductEntityRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.service.ReportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock private OrderEntityRepository orderRepository;
    @Mock private OrderProductEntityRepository orderItemRepository;
    @Mock private ProductRepository productRepository;

    @Test
    void getSalesReport_ShouldCallRepositoryWithCorrectDates() {
        // Arrange
        LocalDate start = LocalDate.of(2023, 1, 1);
        LocalDate end = LocalDate.of(2023, 1, 31);

        when(orderRepository.getSalesReport(any(), any())).thenReturn(List.of(new DTOSalesReport()));

        // Act
        reportService.getSalesReport(start, end);

        // Assert
        // Verifica se converteu LocalDate para LocalDateTime corretamente (Começo e Fim do dia)
        verify(orderRepository).getSalesReport(
                start.atStartOfDay(),
                end.atTime(23, 59, 59)
        );
    }

    @Test
    void getTopSellingProducts_ShouldCallRepository() {
        reportService.getTopSellingProducts();
        verify(orderItemRepository).findTopSellingProducts();
    }

    @Test
    void getLowStockProducts_ShouldCallRepository() {
        reportService.getLowStockProducts(10);
        verify(productRepository).findByStockQuantityLessThan(10);
    }
}