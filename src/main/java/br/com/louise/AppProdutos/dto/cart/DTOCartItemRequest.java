package br.com.louise.AppProdutos.dto;

import lombok.Data;

@Data
public class DTOCartItemRequest {
    private String productId;
    private Integer quantity;
}