package br.com.louise.AppProdutos.scheduler;

import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.service.EmailService;
import br.com.louise.AppProdutos.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockAlertScheduler {

    private final ReportService reportService;
    private final EmailService emailService;
    private final ProductRepository productRepository;

    @Scheduled(fixedRate = 60000)
    public void checkLowStock() {
        log.info("Iniciando verificação agendada de estoque baixo...");
        // 1. Validação de Catálogo Vazio
        long totalProdutos = productRepository.count();
        if (totalProdutos == 0) {
            log.info("Catálogo vazio. Nenhuma verificação necessária.");
            return;
        }

        // 2. Busca produtos com estoque baixo (se o catálogo não estiver vazio)
        List<ProductEntity> lowStockProducts = reportService.getLowStockProducts(5);

        if (lowStockProducts.isEmpty()) {
            log.info("Tudo certo! Temos {} produtos cadastrados e nenhum com estoque crítico.", totalProdutos);
        } else {
            log.warn("ALERTA: Encontrados {} produtos com estoque baixo!", lowStockProducts.size());
            enviarEmailAlerta(lowStockProducts);
        }
    }

    private void enviarEmailAlerta(List<ProductEntity> lowStockProducts) {
        StringBuilder body = new StringBuilder();
        body.append("Atenção, Admin!\n\nOs seguintes produtos estão com estoque crítico:\n");

        for (ProductEntity product : lowStockProducts) {
            body.append(String.format("- %s (ID: %s): Restam %d unidades\n",
                    product.getName(), product.getProductId(), product.getStockQuantity()));
        }
        body.append("\nFavor providenciar reposição imediata.");

        emailService.sendSimpleEmail("admin@appprodutos.com", "Alerta de Estoque Baixo", body.toString());
    }
}