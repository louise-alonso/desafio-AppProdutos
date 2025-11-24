package br.com.louise.AppProdutos.dto.cart;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DTOCartItemRequest {

    @Schema(description = "ID do produto a adicionar", example = "ID_DO_PRODUTO_AQUI")
    @NotBlank
    private String productId;

    @Schema(description = "Quantidade", example = "2")
    @NotNull
    @Min(1)
    private Integer quantity;
}