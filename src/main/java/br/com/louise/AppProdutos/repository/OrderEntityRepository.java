package br.com.louise.AppProdutos.repository;

import br.com.louise.AppProdutos.dto.report.DTOSalesReport;
import br.com.louise.AppProdutos.model.OrderEntity;
import br.com.louise.AppProdutos.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderEntityRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByOrderId(String orderId);

    List<OrderEntity> findAllByOrderByCreatedAtDesc();

    Long countByCustomerAndAppliedCoupon(UserEntity customer, String appliedCoupon);

    // --- RELATÓRIO DE VENDAS DIÁRIAS ---
    // Seleciona: Data, Contagem de Pedidos, Soma do Total
    // Filtra: Por intervalo de datas e apenas pedidos PAGOS ou ENTREGUES
    // Agrupa: Por Data
    @Query("SELECT new br.com.louise.AppProdutos.dto.report.DTOSalesReport(" +
            "CAST(o.createdAt AS LocalDate), COUNT(o), SUM(o.grandTotal)) " +
            "FROM OrderEntity o " +
            "WHERE o.createdAt BETWEEN :startDate AND :endDate " +
            "AND o.status IN ('PAID', 'SHIPPED', 'DELIVERED') " +
            "GROUP BY CAST(o.createdAt AS LocalDate) " +
            "ORDER BY CAST(o.createdAt AS LocalDate) ASC")
    List<DTOSalesReport> getSalesReport(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
}