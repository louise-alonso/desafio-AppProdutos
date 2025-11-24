package br.com.louise.AppProdutos.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp; 


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DTOUserResponse {
    private String userId;
    private String email;
    private String name;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String role; 
}