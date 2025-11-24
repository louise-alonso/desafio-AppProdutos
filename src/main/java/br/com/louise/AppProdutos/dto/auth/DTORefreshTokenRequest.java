package br.com.louise.AppProdutos.dto.auth;

import lombok.Data;

@Data
public class DTORefreshTokenRequest {
    private String refreshToken;
}