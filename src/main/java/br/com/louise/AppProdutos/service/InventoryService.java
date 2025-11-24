package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.dto.inventory.DTOInventoryRequest;
import br.com.louise.AppProdutos.model.InventoryTransactionEntity;

import java.util.List;

public interface InventoryService {
    // Processa movimentação e atualiza saldo do produto
    void processTransaction(DTOInventoryRequest request, String responsibleEmail);

    // Lê o histórico
    List<InventoryTransactionEntity> getHistoryByProduct(String productId);
}