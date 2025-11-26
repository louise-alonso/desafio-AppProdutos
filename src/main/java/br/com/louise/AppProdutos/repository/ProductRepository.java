package br.com.louise.AppProdutos.repository;

import br.com.louise.AppProdutos.model.CategoryEntity;
import br.com.louise.AppProdutos.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByNameIgnoreCase(String name);

    Optional<List<ProductEntity>> findByCategoryId(Long categoryId);

    Optional<ProductEntity> findByProductId(String productId);

    boolean existsBySku(String sku);

    Integer countByCategory(CategoryEntity category);

    // Busca produtos com estoque menor que X
    List<ProductEntity> findByStockQuantityLessThan(Integer minStock);

    long countByActiveTrue();

    List<ProductEntity> findByStockQuantityLessThanAndActiveTrue(int stockQuantity);
}