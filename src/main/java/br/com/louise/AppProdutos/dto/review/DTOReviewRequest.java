package br.com.louise.AppProdutos.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DTOReviewRequest {

    @Schema(description = "ID do Produto", example = "ID_DO_PRODUTO_AQUI")
    @NotBlank
    private String productId;

    @Schema(description = "ID do Pedido (Compra Verificada)", example = "ID_DO_PEDIDO_AQUI")
    @NotBlank
    private String orderId;

    @Schema(description = "Nota (1 a 5)", example = "5")
    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @Schema(description = "Comentário", example = "Produto excelente, chegou rápido!")
    private String comment;
}