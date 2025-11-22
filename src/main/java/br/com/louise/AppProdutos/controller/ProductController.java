package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.DTOProductRequest;
import br.com.louise.AppProdutos.dto.DTOProductResponse;
import br.com.louise.AppProdutos.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ADMIN e SELLER podem criar
    @PostMapping("/admin/products")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    public DTOProductResponse addProduct(@RequestBody DTOProductRequest request) {
        try {
            return productService.add(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao adicionar produto: " + e.getMessage());
        }
    }

    @GetMapping("/products")
    public List<DTOProductResponse> readProducts() {
        return productService.fetchProducts();
    }

    // Deletar: ADMIN ou SELLER (se for dono)
    @DeleteMapping("/admin/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    // Regra: ADMIN ou Dono do produto
    @PreAuthorize("hasRole('ADMIN') or @productPermissionService.isOwner(#productId)")
    public void removeProduct(@PathVariable String productId) {
        try {
            productService.deleteProducts(productId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado");
        }
    }

    // ADMIN e SELLER podem editar
    @PutMapping("/admin/products/{productId}")
    @PreAuthorize("hasRole('ADMIN') or @productPermissionService.isOwner(#productId)") // <--- ALTERADO
    public DTOProductResponse updateProduct(@PathVariable String productId, @RequestBody DTOProductRequest request) {
        try {
            return productService.updateProduct(productId, request);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro ao atualizar produto: " + e.getMessage());
        }
    }
}