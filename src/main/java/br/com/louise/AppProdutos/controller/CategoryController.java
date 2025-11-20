package br.com.louise.AppProdutos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException; 

import br.com.louise.AppProdutos.dto.DTOCategoryRequest;
import br.com.louise.AppProdutos.dto.DTOCategoryResponse;
import br.com.louise.AppProdutos.service.CategoryService;


import jakarta.persistence.EntityNotFoundException; 
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping ("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public DTOCategoryResponse addCategory(@RequestBody DTOCategoryRequest request) {
        return categoryService.add(request);
    }

    @GetMapping("/categories")
    public List<DTOCategoryResponse> fetchCategories() {
        return categoryService.read();
    }

    @DeleteMapping("/admin/categories/{categoryId}") 
    @ResponseStatus(HttpStatus.NO_CONTENT) 
    public void deleteCategory(@PathVariable String categoryId) {
        try {
            categoryService.delete(categoryId);
        } catch (EntityNotFoundException e) { 
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, e.getMessage()
            );
        }
    }
}