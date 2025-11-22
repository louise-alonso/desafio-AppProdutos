package br.com.louise.AppProdutos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DTOProductRequest {
    private String name;
    private BigDecimal price;
    private String categoryId;
    private String description;
    private String sku;
    private BigDecimal costPrice;
    private Integer stockQuantity;

}
