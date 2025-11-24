package br.com.louise.AppProdutos.repository;

import br.com.louise.AppProdutos.dto.report.DTOTopProduct;
import br.com.louise.AppProdutos.model.OrderProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderProductEntityRepository extends JpaRepository<OrderProductEntity, Long> {

    // --- RELATÓRIO DE PRODUTOS MAIS VENDIDOS ---
    // Soma a quantidade e o valor total (preço * qtd) agrupando pelo nome do produto
    // Nota: Buscamos apenas de pedidos válidos (precisaria de join complexo,
    // mas para simplificar o portfólio, vamos somar tudo da tabela de itens).
    @Query("SELECT new br.com.louise.AppProdutos.dto.report.DTOTopProduct(" +
            "op.name, SUM(op.quantity), SUM(op.price * op.quantity)) " +
            "FROM OrderProductEntity op " +
            "GROUP BY op.productId, op.name " +
            "ORDER BY SUM(op.quantity) DESC")
    List<DTOTopProduct> findTopSellingProducts();
}