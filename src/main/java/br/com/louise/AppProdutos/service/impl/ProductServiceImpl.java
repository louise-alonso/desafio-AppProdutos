package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.DTOProductRequest;
import br.com.louise.AppProdutos.dto.DTOProductResponse;
import br.com.louise.AppProdutos.model.CategoryEntity;
import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.model.UserEntity;
import br.com.louise.AppProdutos.repository.CategoryRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.repository.UserRepository;
import br.com.louise.AppProdutos.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

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

        // Define o dono (Owner)
        // Pega o email do usuário autenticado no contexto de segurança
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity owner = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuário proprietário não encontrado."));

        newProduct.setOwner(owner);

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
    public DTOProductResponse readProductById(String productId) {
        ProductEntity existingProduct = productRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado com o id: " + productId));

        return convertToResponse(existingProduct);
    }

    @Override
    public void deleteProducts(String productId) {
        ProductEntity existingProduct = productRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        productRepository.delete(existingProduct);
    }

    @Override
    public DTOProductResponse updateProduct(String productId, DTOProductRequest request) {
        ProductEntity existingProduct = productRepository.findByProductId(productId)
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado para atualização."));

        String emailLogado = SecurityContextHolder.getContext().getAuthentication().getName();
        String roleLogada = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().iterator().next().getAuthority();

        if ("ROLE_SELLER".equals(roleLogada)) {
            // Se o produto não tiver dono ou o dono for diferente do logado -> Bloqueia
            if (existingProduct.getOwner() == null || !existingProduct.getOwner().getEmail().equals(emailLogado)) {
                throw new AccessDeniedException("Você só pode alterar seus próprios produtos.");
            }
        }

        if (!existingProduct.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe outro produto com o SKU: " + request.getSku());
        }

        CategoryEntity newCategory = existingProduct.getCategory();
        if (request.getCategoryId() != null &&
                (existingProduct.getCategory() == null || !existingProduct.getCategory().getCategoryId().equals(request.getCategoryId()))) {

            newCategory = categoryRepository.findByCategoryId(request.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Nova Categoria não encontrada."));
        }

        existingProduct.setName(request.getName());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setSku(request.getSku());
        existingProduct.setCostPrice(request.getCostPrice());
        existingProduct.setStockQuantity(request.getStockQuantity());
        existingProduct.setCategory(newCategory);

        existingProduct = productRepository.save(existingProduct);
        return convertToResponse(existingProduct);
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