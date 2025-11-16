package br.com.louise.AppProdutos.repository;

import br.com.louise.AppProdutos.dto.ProdutoDto;
import br.com.louise.AppProdutos.model.Produtos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProdutosRepository extends JpaRepository<Produtos, Long> {

    //Projection
    @Query(nativeQuery = true, value = """
            SELECT p.id, 
            p.codigo_barras AS codigoBarras, 
            p.nome, 
            p.preco
            FROM tb_produtos p 
            WHERE p.id = :id
            """)
    ProdutoDto findByIdDto(long id);
}

