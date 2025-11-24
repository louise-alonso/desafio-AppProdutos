package br.com.louise.AppProdutos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tbl_order_items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private String name;

    @Column(nullable = false)
    private BigDecimal price; // Preço congelado no momento da compra

    @Column(nullable = false)
    private Integer quantity;
}