package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.DTOProductRequest;
import br.com.louise.AppProdutos.dto.DTOProductResponse;
import br.com.louise.AppProdutos.model.CategoryEntity;
import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.repository.CategoryRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public DTOProductResponse add(DTOProductRequest request) {
        // Validação de SKU
        if (productRepository.existsBySku(request.getSku())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe um produto com o SKU: " + request.getSku());
        }

        CategoryEntity existingCategory = categoryRepository.findByCategoryId(request.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada"));

        ProductEntity newProduct = convertToEntity(request);
        newProduct.setCategory(existingCategory);

        newProduct = productRepository.save(newProduct);

        return convertToResponse(newProduct);
    }

    @Override
    public List<DTOProductResponse> fetchProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProducts(String productId) {
        ProductEntity existingProduct = productRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        productRepository.delete(existingProduct);
    }

    private DTOProductResponse convertToResponse(ProductEntity productEntity) {
        return DTOProductResponse.builder()
                .productId(productEntity.getProductId())
                .name(productEntity.getName())
                .description(productEntity.getDescription())
                .price(productEntity.getPrice())
                .sku(productEntity.getSku())
                .costPrice(productEntity.getCostPrice())
                .stockQuantity(productEntity.getStockQuantity())
                .active(productEntity.getActive())
                .categoryName(productEntity.getCategory() != null ? productEntity.getCategory().getName() : null)
                .categoryId(productEntity.getCategory() != null ? productEntity.getCategory().getCategoryId() : null)
                .createdAt(productEntity.getCreatedAt())
                .updatedAt(productEntity.getUpdatedAt())
                .build();
    }

    private ProductEntity convertToEntity(DTOProductRequest request) {
        return ProductEntity.builder()
                .productId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .sku(request.getSku())
                .costPrice(request.getCostPrice())
                .stockQuantity(request.getStockQuantity())
                .active(true)
                .build();
    }
}