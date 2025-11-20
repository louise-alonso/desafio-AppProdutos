package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.dto.DTOProductRequest;
import br.com.louise.AppProdutos.dto.DTOProductResponse;

import java.util.List;

public interface ProductService {

    DTOProductResponse add(DTOProductRequest request);

    List<DTOProductResponse> fetchProducts();

    void deleteProducts (String productId);


}
