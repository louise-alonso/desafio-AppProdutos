package br.com.louise.AppProdutos.dto;

import java.sql.Timestamp;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponse {
    private String categoriaId;
    private String nome;
    private String descricao;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}