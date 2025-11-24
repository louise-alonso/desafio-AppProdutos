package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.category.DTOCategoryRequest;
import br.com.louise.AppProdutos.dto.category.DTOCategoryResponse;
import br.com.louise.AppProdutos.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation; // <--- IMPORTANTE
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
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "02. Categorias", description = "Gestão de categorias de produtos")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar nova categoria", description = "Cadastra uma categoria no sistema. O nome deve ser único.")
    public DTOCategoryResponse addCategory(@RequestBody @Valid DTOCategoryRequest request) {
        return categoryService.add(request);
    }

    @GetMapping
    @Operation(summary = "Listar todas", description = "Retorna a lista completa de categorias cadastradas. Acesso público.")
    public List<DTOCategoryResponse> fetchCategories() {
        return categoryService.read();
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Buscar por ID", description = "Retorna os detalhes de uma categoria específica.")
    public DTOCategoryResponse fetchCategoryById(@PathVariable String categoryId) {
        try {
            return categoryService.readById(categoryId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada");
        }
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Excluir categoria", description = "Remove uma categoria pelo ID. **Regra:** Não é possível excluir se houver produtos vinculados a ela.")
    public void deleteCategory(@PathVariable String categoryId) {
        try {
            categoryService.delete(categoryId);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            // Captura o erro de integridade (DataIntegrityViolation)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{categoryId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atualizar categoria", description = "Atualiza nome ou descrição de uma categoria existente.")
    public DTOCategoryResponse updateCategory(@PathVariable String categoryId, @RequestBody @Valid DTOCategoryRequest request) {
        try {
            return categoryService.update(categoryId, request);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}