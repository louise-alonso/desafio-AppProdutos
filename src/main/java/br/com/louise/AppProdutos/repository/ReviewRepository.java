package br.com.louise.AppProdutos.repository;

import br.com.louise.AppProdutos.model.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewEntity, Long> {

    // Buscar todas as avaliações de um produto
    List<ReviewEntity> findByProductProductIdOrderByCreatedAtDesc(String productId);

    // Verificar se já existe avaliação deste pedido para este produto
    boolean existsByOrderIdAndProductProductId(String orderId, String productId);
}