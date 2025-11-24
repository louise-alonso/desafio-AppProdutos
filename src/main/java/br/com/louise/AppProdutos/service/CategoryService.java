package br.com.louise.AppProdutos.service;

import java.util.List;

import br.com.louise.AppProdutos.dto.category.DTOCategoryRequest;
import br.com.louise.AppProdutos.dto.category.DTOCategoryResponse;

public interface CategoryService {
    
    DTOCategoryResponse add(DTOCategoryRequest request);

    DTOCategoryResponse update(String categoryId, DTOCategoryRequest request);

    DTOCategoryResponse readById(String categoryId);

    List<DTOCategoryResponse> read();

    void delete(String categoryId);
}