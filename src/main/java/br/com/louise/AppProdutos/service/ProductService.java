package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.dto.product.DTOProductRequest;
import br.com.louise.AppProdutos.dto.product.DTOProductResponse;

import java.util.List;

public interface ProductService {

    DTOProductResponse add(DTOProductRequest request);

    List<DTOProductResponse> fetchProducts();

    void deleteProducts(String productId);

    DTOProductResponse readProductById(String productId);

    DTOProductResponse updateProduct(String productId, DTOProductRequest request);

}