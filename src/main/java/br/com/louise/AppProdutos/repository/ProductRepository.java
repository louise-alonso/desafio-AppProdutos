package br.com.louise.AppProdutos.repository;

import br.com.louise.AppProdutos.model.CategoryEntity;
import br.com.louise.AppProdutos.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {

    Optional<ProductEntity> findByProductId(String productId);

    boolean existsBySku(String sku);

    Integer countByCategory(CategoryEntity category);
}