package br.com.louise.AppProdutos.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOProductResponse implements Serializable {
    private static final long serialVersionUID = 1L;

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

    private Double averageRating;
    private Integer reviewCount;
}