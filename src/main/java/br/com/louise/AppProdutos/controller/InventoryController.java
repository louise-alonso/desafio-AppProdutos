package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.inventory.DTOInventoryRequest;
import br.com.louise.AppProdutos.model.InventoryTransactionEntity;
import br.com.louise.AppProdutos.service.InventoryService;
import jakarta.validation.Valid; // <--- Importante
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/adjust")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public void adjustInventory(@RequestBody @Valid DTOInventoryRequest request) { // <--- @Valid adicionado
        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        inventoryService.processTransaction(request, emailLogado);
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public List<InventoryTransactionEntity> getHistory(@PathVariable String productId) {
        return inventoryService.getHistoryByProduct(productId);
    }
}