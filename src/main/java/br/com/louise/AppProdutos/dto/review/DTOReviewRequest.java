package br.com.louise.AppProdutos.dto.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DTOReviewRequest {

    @NotBlank(message = "O ID do produto é obrigatório")
    private String productId;

    @NotBlank(message = "O ID do pedido é obrigatório")
    private String orderId;

    @NotNull(message = "A nota é obrigatória")
    @Min(value = 1, message = "A nota mínima é 1")
    @Max(value = 5, message = "A nota máxima é 5")
    private Integer rating;

    private String comment;
}