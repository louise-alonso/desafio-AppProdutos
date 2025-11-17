package br.com.louise.AppProdutos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryRequest {
    private String nome;
    private String descricao;
}