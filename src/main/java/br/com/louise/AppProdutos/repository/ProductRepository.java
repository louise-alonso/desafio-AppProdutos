package br.com.louise.AppProdutos.repository;

import br.com.louise.AppProdutos.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByProductId(String productId);

    Integer countByCategoryId(Long id);

    boolean existsBySku(String sku);
}