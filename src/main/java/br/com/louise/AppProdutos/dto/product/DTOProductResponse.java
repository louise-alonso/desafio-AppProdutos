package br.com.louise.AppProdutos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOProductResponse {
    private String productId;
    private String name;
    private BigDecimal price;
    private String categoryId;
    private String description;
    private String categoryName;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String sku;
    private BigDecimal costPrice;
    private Integer stockQuantity;
    private Boolean active;
}