package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.category.DTOCategoryRequest;
import br.com.louise.AppProdutos.dto.category.DTOCategoryResponse;
import br.com.louise.AppProdutos.model.CategoryEntity;
import br.com.louise.AppProdutos.repository.CategoryRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @Test
    void add_ShouldCreateCategory_WhenValid() {
        DTOCategoryRequest request = new DTOCategoryRequest();
        request.setName("Eletrônicos");
        request.setDescription("Gadgets em geral");

        when(categoryRepository.save(any(CategoryEntity.class))).thenAnswer(i -> {
            CategoryEntity c = i.getArgument(0);
            c.setId(1L);
            return c;
        });

        DTOCategoryResponse response = categoryService.add(request);

        assertNotNull(response);
        assertEquals("Eletrônicos", response.getName());
        verify(categoryRepository).save(any());
    }

    @Test
    void add_ShouldThrowException_WhenParentNotFound() {
        DTOCategoryRequest request = new DTOCategoryRequest();
        request.setName("SubCategoria");
        request.setParentId("parent-inexistente");

        when(categoryRepository.findByCategoryId("parent-inexistente")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> categoryService.add(request));
    }

    @Test
    void delete_ShouldThrowException_WhenIntegrityViolationOccurs() {
        String catId = "cat-com-produtos";
        CategoryEntity category = new CategoryEntity();

        when(categoryRepository.findByCategoryId(catId)).thenReturn(Optional.of(category));
        doThrow(new DataIntegrityViolationException("Constraint violation"))
                .when(categoryRepository).delete(category);

        assertThrows(ResponseStatusException.class, () -> categoryService.delete(catId));
    }

    @Test
    void update_ShouldThrowException_WhenCategoryIsParentOfItself() {
        String catId = "cat-1";
        DTOCategoryRequest request = new DTOCategoryRequest();
        request.setName("Loop");
        request.setParentId(catId); // Aponta para si mesma

        CategoryEntity category = new CategoryEntity();
        category.setCategoryId(catId);

        when(categoryRepository.findByCategoryId(catId)).thenReturn(Optional.of(category));

        assertThrows(ResponseStatusException.class, () -> categoryService.update(catId, request));
    }

    @Test
    void read_ShouldReturnListOfCategories() {
        when(categoryRepository.findAll()).thenReturn(List.of(new CategoryEntity()));
        List<DTOCategoryResponse> result = categoryService.read();
        assertFalse(result.isEmpty());
    }
}