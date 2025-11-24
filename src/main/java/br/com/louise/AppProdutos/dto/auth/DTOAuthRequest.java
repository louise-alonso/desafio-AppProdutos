package br.com.louise.AppProdutos.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTOAuthRequest {
    private String email;
    private String password;
}
