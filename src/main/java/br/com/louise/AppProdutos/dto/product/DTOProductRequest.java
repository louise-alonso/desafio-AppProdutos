package br.com.louise.AppProdutos.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Nome do Produto", example = "Smartphone Galaxy S23")
    @NotBlank
    private String name;

    @Schema(description = "Preço de Venda", example = "4500.00")
    @NotNull
    @Positive
    private BigDecimal price;

    @Schema(description = "ID da Categoria (deve existir)", example = "cat-eletronicos-01")
    @NotBlank
    private String categoryId;

    @Schema(description = "Descrição detalhada", example = "256GB, Tela AMOLED, Preto")
    private String description;

    @Schema(description = "Código SKU único", example = "S23-BLK-256")
    @NotBlank
    private String sku;

    @Schema(description = "Preço de Custo", example = "3000.00")
    private BigDecimal costPrice;

    @Schema(description = "Estoque inicial", example = "50")
    @NotNull
    @Positive
    private Integer stockQuantity;
}