package br.com.louise.AppProdutos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOAuthResponse {
    private String email;
    private String role;
    private String accessToken;
    private String refreshToken;
}