package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.product.DTOProductRequest;
import br.com.louise.AppProdutos.dto.product.DTOProductResponse;
import br.com.louise.AppProdutos.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Tag(name = "03. Produtos", description = "Catálogo de vendas")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/admin/products")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @Operation(summary = "Cadastrar produto", description = "Cria um novo produto no catálogo. O utilizador logado será definido como proprietário (Seller).")
    public DTOProductResponse addProduct(@RequestBody @Valid DTOProductRequest request) {
        try {
            return productService.add(request);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/products")
    @Operation(summary = "Listar catálogo", description = "Retorna todos os produtos disponíveis. Acesso público.")
    public List<DTOProductResponse> readProducts() {
        return productService.fetchProducts();
    }

    @GetMapping("/products/{productId}")
    @Operation(summary = "Detalhes do produto", description = "Busca informações detalhadas de um produto específico pelo ID.")
    public DTOProductResponse getProductById(@PathVariable String productId) {
        try {
            return productService.readProductById(productId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado");
        }
    }

    @DeleteMapping("/admin/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @Operation(summary = "Excluir produto", description = "Remove um produto. Regra: Seller só pode excluir seus próprios produtos.")
    public void removeProduct(@PathVariable String productId) {
        try {
            productService.deleteProducts(productId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado ou erro ao deletar");
        }
    }

    @PutMapping("/admin/products/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER')")
    @Operation(summary = "Atualizar produto", description = "Altera dados do produto (preço, stock, nome). Regra: Seller só pode editar seus próprios produtos.")
    public DTOProductResponse updateProduct(@PathVariable String productId, @RequestBody @Valid DTOProductRequest request) {
        try {
            return productService.updateProduct(productId, request);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}