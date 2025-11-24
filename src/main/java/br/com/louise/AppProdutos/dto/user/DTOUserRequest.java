package br.com.louise.AppProdutos.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOUserRequest {

    @Schema(description = "Email do usuário", example = "admin@loja.com")
    @NotBlank
    @Email
    private String email;

    @Schema(description = "Senha segura", example = "123456")
    @NotBlank
    @Size(min = 6)
    private String password;

    @Schema(description = "Nome completo", example = "Administrador do Sistema")
    @NotBlank
    private String name;

    @Schema(description = "Perfil de acesso (ADMIN, SELLER, CUSTOMER)", example = "ADMIN")
    private String role;
}