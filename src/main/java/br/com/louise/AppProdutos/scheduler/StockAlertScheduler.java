package br.com.louise.AppProdutos.scheduler;

import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.service.EmailService;
import br.com.louise.AppProdutos.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockAlertScheduler {

    private final ReportService reportService;
    private final EmailService emailService;
    private final ProductRepository productRepository;

    @Value("${app.stock.alert.threshold:5}") // Configurável
    private int stockThreshold;

    @Value("${app.stock.alert.email:admin@appprodutos.com}")
    private String alertEmail;

    private final Set<String> alreadyAlertedProducts = ConcurrentHashMap.newKeySet();

    @Scheduled(fixedRate = 60000) // A cada 1 minuto
    public void checkLowStock() {
        log.info("Iniciando verificação agendada de estoque baixo (threshold: {})...", stockThreshold);

        // 1. Validação de Catálogo Vazio
        long totalProdutos = productRepository.countByActiveTrue();
        if (totalProdutos == 0) {
            log.info("Catálogo vazio. Nenhuma verificação necessária.");
            return;
        }

        // 2. Busca produtos ATIVOS com estoque baixo
        List<ProductEntity> lowStockProducts = reportService.getLowStockProducts(stockThreshold);

        if (lowStockProducts.isEmpty()) {
            log.info("Tudo certo! Temos {} produtos ativos e nenhum com estoque crítico.", totalProdutos);
            alreadyAlertedProducts.clear(); // Reset se estoque normalizou
        } else {
            // Filtra apenas produtos que ainda não foram alertados
            List<ProductEntity> newLowStockProducts = lowStockProducts.stream()
                    .filter(product -> !alreadyAlertedProducts.contains(product.getProductId()))
                    .collect(Collectors.toList());

            if (!newLowStockProducts.isEmpty()) {
                log.warn("ALERTA: Encontrados {} produtos com estoque baixo!", newLowStockProducts.size());
                newLowStockProducts.forEach(product ->
                        log.warn(" - {} (ID: {}): Estoque = {}",
                                product.getName(), product.getProductId(), product.getStockQuantity()));

                enviarEmailAlerta(newLowStockProducts);

                // Marca como já alertados
                newLowStockProducts.forEach(product ->
                        alreadyAlertedProducts.add(product.getProductId()));
            } else {
                log.info("Produtos com estoque baixo já foram alertados anteriormente.");
            }
        }
    }

    private void enviarEmailAlerta(List<ProductEntity> lowStockProducts) {
        StringBuilder body = new StringBuilder();
        body.append("<h2>⚠️ Alerta de Estoque Baixo</h2>");
        body.append("<p>Atenção, Admin!</p>");
        body.append("<p>Os seguintes produtos estão com estoque crítico:</p>");
        body.append("<ul>");

        for (ProductEntity product : lowStockProducts) {
            body.append(String.format("<li><b>%s</b> (ID: %s): Restam <span style='color: red;'>%d unidades</span></li>",
                    product.getName(), product.getProductId(), product.getStockQuantity()));
        }

        body.append("</ul>");
        body.append("<p><b>Favor providenciar reposição imediata.</b></p>");
        body.append("<br><p><i>Este é um alerta automático do sistema AppProdutos</i></p>");

        emailService.sendSimpleEmail(alertEmail, "🚨 Alerta de Estoque Baixo - AppProdutos", body.toString());
    }
}