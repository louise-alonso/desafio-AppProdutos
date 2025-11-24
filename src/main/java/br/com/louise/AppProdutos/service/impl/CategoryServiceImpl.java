package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.category.DTOCategoryRequest;
import br.com.louise.AppProdutos.dto.category.DTOCategoryResponse;
import br.com.louise.AppProdutos.model.CategoryEntity;
import br.com.louise.AppProdutos.repository.CategoryRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public DTOCategoryResponse add(DTOCategoryRequest request) {
        CategoryEntity newCategory = convertToEntity(request);

        if (request.getParentId() != null && !request.getParentId().isBlank()) {
            CategoryEntity parent = categoryRepository.findByCategoryId(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria pai não encontrada: " + request.getParentId()));

            newCategory.setParent(parent);
        }

        newCategory = categoryRepository.save(newCategory);
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
    public DTOCategoryResponse readById(String categoryId) {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com o id: " + categoryId));

        return convertToResponse(existingCategory);
    }

    @Override
    public void delete(String categoryId) {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada com o id: " + categoryId));

        try {
            categoryRepository.delete(existingCategory);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Não é possível deletar. A categoria possui produtos ou subcategorias associadas."
            );
        }
    }

    @Override
    public DTOCategoryResponse update(String categoryId, DTOCategoryRequest request) {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada para atualização: " + categoryId));

        existingCategory.setName(request.getName());
        existingCategory.setDescription(request.getDescription());

        // Atualizar hierarquia se necessário
        if (request.getParentId() != null && !request.getParentId().isBlank()) {
            // Evitar ciclo (uma categoria não pode ser pai dela mesma)
            if(request.getParentId().equals(categoryId)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uma categoria não pode ser pai de si mesma.");
            }
            CategoryEntity parent = categoryRepository.findByCategoryId(request.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Categoria pai não encontrada"));
            existingCategory.setParent(parent);
        } else {
            existingCategory.setParent(null); // Remove o pai se vier nulo
        }

        existingCategory = categoryRepository.save(existingCategory);
        return convertToResponse(existingCategory);
    }

    private CategoryEntity convertToEntity(DTOCategoryRequest request) {
        return CategoryEntity.builder()
                .categoryId(UUID.randomUUID().toString())
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }

    private DTOCategoryResponse convertToResponse(CategoryEntity category) {
        Integer productsCount = productRepository.countByCategory(category);

        return DTOCategoryResponse.builder()
                .categoryId(category.getCategoryId())
                .name(category.getName())
                .description(category.getDescription())
                .products(productsCount)
                .parentName(category.getParent() != null ? category.getParent().getName() : null)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}