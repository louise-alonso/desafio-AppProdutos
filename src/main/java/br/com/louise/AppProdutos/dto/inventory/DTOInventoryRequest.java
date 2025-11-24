package br.com.louise.AppProdutos.dto.inventory;

import br.com.louise.AppProdutos.model.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DTOInventoryRequest {

    @Schema(description = "ID do Produto", example = "ID_DO_PRODUTO_AQUI")
    @NotBlank
    private String productId;

    @Schema(description = "Quantidade a ajustar", example = "10")
    @NotNull
    @Min(1)
    private Integer quantity;

    @Schema(description = "Tipo de Movimentação (ENTRY, EXIT, ADJUSTMENT)", example = "ENTRY")
    @NotNull
    private TransactionType type;

    @Schema(description = "Motivo", example = "Chegada de fornecedor")
    private String description;
}