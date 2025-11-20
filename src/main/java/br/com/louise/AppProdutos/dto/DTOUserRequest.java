package br.com.louise.AppProdutos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOUserRequest {

    private String email;
    private String password;
    private String name;
    private String role;

    
}
