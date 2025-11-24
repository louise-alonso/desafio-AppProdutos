package br.com.louise.AppProdutos.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DTOAuthRequest {

    @Schema(description = "Email cadastrado", example = "admin@loja.com")
    private String email;

    @Schema(description = "Senha", example = "123456")
    private String password;
}