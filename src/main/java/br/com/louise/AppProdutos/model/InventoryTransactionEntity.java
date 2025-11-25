package br.com.louise.AppProdutos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_inventory_transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InventoryTransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Qual produto sofreu a alteração?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    // Quantidade movimentada (sempre positiva aqui, o sinal depende do Type)
    @Column(nullable = false)
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    // Motivo (Ex: "Pedido #123", "Chegada de Fornecedor", "Produto Quebrado")
    private String description;

    // Quem fez a alteração (para auditoria)
    private String responsibleEmail;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private Integer stockAfterTransaction;

}