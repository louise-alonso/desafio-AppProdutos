package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.category.DTOCategoryRequest;
import br.com.louise.AppProdutos.dto.category.DTOCategoryResponse;
import br.com.louise.AppProdutos.service.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // POST /categories (Requer ADMIN)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public DTOCategoryResponse addCategory(@RequestBody @Valid DTOCategoryRequest request) {
        return categoryService.add(request);
    }

    // GET /categories (Público, conforme SecurityConfig)
    @GetMapping
    public List<DTOCategoryResponse> fetchCategories() {
        return categoryService.read();
    }

    // GET /categories/{id} (Público)
    @GetMapping("/{categoryId}")
    public DTOCategoryResponse fetchCategoryById(@PathVariable String categoryId) {
        try {
            return categoryService.readById(categoryId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada");
        }
    }

    // DELETE /categories/{id} (Requer ADMIN)
    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteCategory(@PathVariable String categoryId) {
        try {
            categoryService.delete(categoryId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // PUT /categories/{id} (Requer ADMIN)
    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    public DTOCategoryResponse updateCategory(@PathVariable String categoryId, @RequestBody @Valid DTOCategoryRequest request) {
        try {
            return categoryService.update(categoryId, request);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}