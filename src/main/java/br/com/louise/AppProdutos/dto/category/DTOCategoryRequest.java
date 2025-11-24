package br.com.louise.AppProdutos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DTOCategoryRequest {
    private String name;
    private String description;
    private String parentId;
}