package br.com.louise.AppProdutos.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException; 
import org.springframework.stereotype.Service;

import br.com.louise.AppProdutos.dto.CategoryRequest;
import br.com.louise.AppProdutos.dto.CategoryResponse;

import br.com.louise.AppProdutos.model.CategoryEntity;
import br.com.louise.AppProdutos.repository.CategoryRepository;
import br.com.louise.AppProdutos.service.CategoryService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse add(CategoryRequest request) {
        CategoryEntity newCategory = convertToEntity(request);
        newCategory = categoryRepository.save(newCategory);
        return convertToResponse(newCategory);
    }

    @Override
    public List<CategoryResponse> read() {
        return categoryRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    private CategoryEntity convertToEntity(CategoryRequest request) {
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getNome()) 
                .description(request.getDescricao()) 
                .build();
    }

    @Override
    public void delete(String categoryId) { 
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com o id: " + categoryId));

        categoryRepository.delete(existingCategory);
    }

    private CategoryResponse convertToResponse(CategoryEntity category) {
        return CategoryResponse.builder()
                .categoriaId(category.getCategoryId())
                .nome(category.getName()) 
                .descricao(category.getDescription()) 
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}