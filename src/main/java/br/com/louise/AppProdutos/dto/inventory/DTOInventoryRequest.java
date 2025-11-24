package br.com.louise.AppProdutos.dto.inventory;

import br.com.louise.AppProdutos.model.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DTOInventoryRequest {

    @NotBlank(message = "O ID do produto é obrigatório")
    private String productId;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    private Integer quantity;

    @NotNull(message = "O tipo de transação é obrigatório")
    private TransactionType type;

    private String description;
}