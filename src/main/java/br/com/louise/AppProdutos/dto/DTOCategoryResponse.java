package br.com.louise.AppProdutos.dto;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DTOCategoryResponse {
    private String categoryId;
    private String name;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Integer products;

    private String parentName;
}