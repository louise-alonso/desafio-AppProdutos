package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.DTOProductRequest;
import br.com.louise.AppProdutos.dto.DTOProductResponse;
import br.com.louise.AppProdutos.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    // Dica: Geralmente o Controller só fala com o Service, não com o Repository direto.
    private final ProductService productService;

    // Rota de ADMIN (Criar)
    @PostMapping("/admin/products")
    @ResponseStatus(HttpStatus.CREATED)
    public DTOProductResponse addProduct(@RequestBody DTOProductRequest request) {
        try {
            return productService.add(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao adicionar produto: " + e.getMessage());
        }
    }

    // Rota PÚBLICA (Ler)
    @GetMapping("/products")
    public List<DTOProductResponse> readProducts() {
        return productService.fetchProducts();
    }

    // Rota de ADMIN (Deletar)
    @DeleteMapping("/admin/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProduct(@PathVariable String productId) {
        try {
            productService.deleteProducts(productId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado");
        }
    }
}