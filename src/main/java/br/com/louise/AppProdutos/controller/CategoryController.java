package br.com.louise.AppProdutos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import br.com.louise.AppProdutos.dto.DTOCategoryRequest;
import br.com.louise.AppProdutos.dto.DTOCategoryResponse;
import br.com.louise.AppProdutos.service.CategoryService;


import jakarta.persistence.EntityNotFoundException; 
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping ("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public DTOCategoryResponse addCategory(@RequestBody DTOCategoryRequest request) {
        return categoryService.add(request);
    }

    @GetMapping("/categories")
    public List<DTOCategoryResponse> fetchCategories() {
        return categoryService.read();
    }

    @GetMapping("/categories/{categoryId}")
    public DTOCategoryResponse fetchCategoryById(@PathVariable String categoryId) {
        try {
            return categoryService.readById(categoryId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Categoria não encontrada"
            );
        }
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteCategory(@PathVariable String categoryId) {
        try {
            categoryService.delete(categoryId);
        } catch (EntityNotFoundException e) { 
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, e.getMessage()
            );
        }
    }

    @PutMapping("/admin/categories/{categoryId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public DTOCategoryResponse updateCategory(@PathVariable String categoryId, @RequestBody DTOCategoryRequest request) {
        try {
            return categoryService.update(categoryId, request); // Chamada ao novo método
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            // Pode ser erro de hierarquia ou nome duplicado
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}