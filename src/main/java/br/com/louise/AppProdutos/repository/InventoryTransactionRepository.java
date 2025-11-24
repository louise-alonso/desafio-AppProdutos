package br.com.louise.AppProdutos.repository;

import br.com.louise.AppProdutos.model.InventoryTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransactionEntity, Long> {
    // Buscar histórico de um produto específico ordenado por data
    List<InventoryTransactionEntity> findByProductProductIdOrderByCreatedAtDesc(String productId);
}