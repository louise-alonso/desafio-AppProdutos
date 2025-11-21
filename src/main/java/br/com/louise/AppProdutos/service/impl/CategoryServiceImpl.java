package br.com.louise.AppProdutos.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import br.com.louise.AppProdutos.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import br.com.louise.AppProdutos.dto.DTOCategoryRequest;
import br.com.louise.AppProdutos.dto.DTOCategoryResponse;

import br.com.louise.AppProdutos.model.CategoryEntity;
import br.com.louise.AppProdutos.repository.CategoryRepository;
import br.com.louise.AppProdutos.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Override
    public DTOCategoryResponse add(DTOCategoryRequest request) {
        CategoryEntity newCategory = convertToEntity(request);

        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta aplicação não permite hierarquia entre categorias. A categoria deve ser um nó Raiz.");
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

    public DTOCategoryResponse update(String categoryId, DTOCategoryRequest request) {
        CategoryEntity existingCategory = categoryRepository.findByCategoryId(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Categoria não encontrada para atualização: " + categoryId));

        if (request.getParentId() != null && !request.getParentId().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta aplicação não permite hierarquia entre categorias. O campo 'parentId' deve ser omitido.");
        }

        existingCategory.setName(request.getName());
        existingCategory.setDescription(request.getDescription());

        existingCategory = categoryRepository.save(existingCategory);

        return convertToResponse(existingCategory);
    }
}