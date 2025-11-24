package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.inventory.DTOInventoryRequest;
import br.com.louise.AppProdutos.model.InventoryTransactionEntity;
import br.com.louise.AppProdutos.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Tag(name = "05. Estoque (Inventory)", description = "Ajustes e auditoria de movimentações")
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/adjust")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @Operation(summary = "Ajuste manual de estoque", description = "Permite dar entrada ou saída manual de produtos (ex: Reposição, Perda, Doação). Requer ADMIN ou SELLER.")
    public void adjustInventory(@RequestBody @Valid DTOInventoryRequest request) {
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        inventoryService.processTransaction(request, emailLogado);
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @Operation(summary = "Histórico de movimentações", description = "Lista todas as transações (Entradas/Saídas) de um produto específico para fins de auditoria.")
    public List<InventoryTransactionEntity> getHistory(@PathVariable String productId) {
        return inventoryService.getHistoryByProduct(productId);
    }
}