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

import br.com.louise.AppProdutos.dto.CategoryRequest;
import br.com.louise.AppProdutos.dto.CategoryResponse;
import br.com.louise.AppProdutos.service.CategoryService;


import jakarta.persistence.EntityNotFoundException; 
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping ("/admin/categorias")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse addCategory(@RequestBody CategoryRequest request) {
        return categoryService.add(request);
    }

    @GetMapping("/categorias")
    public List<CategoryResponse> fetchCategories() {
        return categoryService.read();
    }

    @DeleteMapping("/admin/categorias/{categoryId}") 
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