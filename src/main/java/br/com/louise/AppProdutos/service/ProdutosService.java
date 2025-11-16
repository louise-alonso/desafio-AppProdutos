package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.model.Produtos;
import br.com.louise.AppProdutos.repository.ProdutosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProdutosService {

    private final ProdutosRepository produtosRepository;

    public Optional<Produtos> atualizaProduto(Produtos produto) {
        log.info("Atualizando produto: {}", produto);
        final var produtoExistente = produtosRepository.findById(produto.getId());
        produtoExistente.ifPresent(p -> {
            produto.setCodigoBarras(p.getCodigoBarras());
            produto.setNome(p.getNome());
            produto.setPreco(p.getPreco());
            produtosRepository.saveAndFlush(produto);
        });
        return produtoExistente;
    }

}
