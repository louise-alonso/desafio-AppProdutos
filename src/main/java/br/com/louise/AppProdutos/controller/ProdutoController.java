package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.ProdutoDto;
import br.com.louise.AppProdutos.model.Produtos;
import br.com.louise.AppProdutos.repository.ProdutosRepository;
import br.com.louise.AppProdutos.service.ProdutosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/produtos/")
public class ProdutoController {

    private final ProdutosRepository produtosRepository;
    private final ProdutosService produtosService;

    @PostMapping("produto")
    public ResponseEntity<Produtos> criaProduto(@RequestBody Produtos produto) {
        Produtos saved = produtosRepository.save(produto);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Produtos>> listaProdutos() {
        List<Produtos> produtos = produtosRepository.findAll();
        return ResponseEntity.ok(produtos);
    }

    @GetMapping("{id}")
    public ResponseEntity<Produtos> listaProdutoPorId(@PathVariable Long id) {
        Produtos produto = produtosRepository.findById(id).orElseThrow();
        return ResponseEntity.ok(produto);
    }

    /**
     * Exemplo de retorno de um Record.
     * @param id
     * @return
     */
    @GetMapping("/dto/{id}")
    public ResponseEntity<ProdutoDto> listaProdutoDtoPorId(@PathVariable Long id) {
        ProdutoDto produtoDto = produtosRepository.findByIdDto(id);

        final var produto = new Produtos();
        produto.setNome(produtoDto.nome());
        produto.setPreco(produtoDto.preco());
        produto.setCodigoBarras(produtoDto.codigoBarras());
        produtosRepository.saveAndFlush(produto);

        return ResponseEntity.ok(produtoDto);
    }

    @PutMapping("atualiza")
    public ResponseEntity<Optional<Produtos>> atualizaProduto(@RequestBody Produtos produto) {
        final var produtoExistente = produtosService.atualizaProduto(produto);
        return ResponseEntity.ok(produtoExistente);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deletaProduto(@PathVariable Long id) {
        //Exemplo construindo Record
        final var p = new ProdutoDto(1L, "dfs", "sdfa", new BigDecimal("25.6"));
        //Exemplo construindo Builder do record
        final var p2 = ProdutoDto.builder()
                .id(1L)
                .codigoBarras("dfs")
                .nome("sdfa")
                .preco(new BigDecimal("25.6"))
                .build();
        produtosRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
