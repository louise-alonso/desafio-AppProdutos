package br.com.louise.AppProdutos.service;

import java.util.List;

import br.com.louise.AppProdutos.dto.CategoryRequest;
import br.com.louise.AppProdutos.dto.CategoryResponse;

public interface CategoryService {
    
    CategoryResponse add(CategoryRequest request);

    List<CategoryResponse> read();

    void delete(String categoryId);
}