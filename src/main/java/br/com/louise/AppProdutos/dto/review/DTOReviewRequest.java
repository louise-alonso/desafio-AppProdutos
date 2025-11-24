package br.com.louise.AppProdutos.dto;

import lombok.Data;

@Data
public class DTOReviewRequest {
    private String productId;
    private String orderId; // O usuário precisa informar de qual compra ele está falando
    private Integer rating; // 1-5
    private String comment;
}