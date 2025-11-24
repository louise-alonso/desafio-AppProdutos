package br.com.louise.AppProdutos.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOCategoryRequest {

    @Schema(description = "Nome da Categoria", example = "Eletrônicos")
    @NotBlank(message = "O nome da categoria é obrigatório")
    private String name;

    @Schema(description = "Descrição curta", example = "Smartphones, Tablets e Acessórios")
    private String description;

    @Schema(description = "ID da categoria Pai (para subcategorias)", example = "")
    private String parentId;
}