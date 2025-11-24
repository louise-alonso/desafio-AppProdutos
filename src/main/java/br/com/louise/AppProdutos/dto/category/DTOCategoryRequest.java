package br.com.louise.AppProdutos.dto.category;

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

    @NotBlank(message = "O nome da categoria é obrigatório")
    private String name;

    private String description;

    private String parentId;
}