package br.com.louise.AppProdutos.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.louise.AppProdutos.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import br.com.louise.AppProdutos.dto.DTOCategoryRequest;
import br.com.louise.AppProdutos.dto.DTOCategoryResponse;

import br.com.louise.AppProdutos.model.CategoryEntity;
import br.com.louise.AppProdutos.repository.CategoryRepository;
import br.com.louise.AppProdutos.service.CategoryService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public DTOCategoryResponse add(DTOCategoryRequest request) {
        // 1. Converte o DTO básico para Entidade
        CategoryEntity newCategory = convertToEntity(request);

        // 2. Lógica de Hierarquia: Se tiver ID do pai, busca e associa
        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            CategoryEntity parentCategory = categoryRepository.findByCategoryId(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria Pai não encontrada com ID: " + request.getParentId()));

            newCategory.setParent(parentCategory);
        }

        // 3. Salva no banco
        newCategory = categoryRepository.save(newCategory);

        // 4. Retorna convertido
        return convertToResponse(newCategory);
    }

    @Override
    public List<DTOCategoryResponse> read() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String categoryId) {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com o id: " + categoryId));

        categoryRepository.delete(existingCategory);
    }

    private CategoryEntity convertToEntity(DTOCategoryRequest request) {
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    private DTOCategoryResponse convertToResponse(CategoryEntity category) {
        Integer productsCount = productRepository.countByCategoryId(category.getId());

        return DTOCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .products(productsCount)
                // Lógica para mostrar o nome do pai (se existir) ou null
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .build();
    }
}