package br.com.louise.AppProdutos.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotBlank(message = "O nome do produto é obrigatório")
    private String name;

    @NotNull(message = "O preço é obrigatório")
    @Positive(message = "O preço deve ser maior que zero")
    private BigDecimal price;

    @NotBlank(message = "O ID da categoria é obrigatório")
    private String categoryId;

    private String description;

    @NotBlank(message = "O SKU é obrigatório")
    private String sku;

    private BigDecimal costPrice;

    @NotNull(message = "A quantidade em estoque é obrigatória")
    @Positive(message = "O estoque deve ser positivo")
    private Integer stockQuantity;
}