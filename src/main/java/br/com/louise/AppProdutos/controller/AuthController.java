package br.com.louise.AppProdutos.controller;

// --- NOVOS IMPORTS CORRETOS ---
import br.com.louise.AppProdutos.dto.auth.DTOAuthRequest;
import br.com.louise.AppProdutos.dto.auth.DTOAuthResponse;
import br.com.louise.AppProdutos.dto.auth.DTORefreshTokenRequest;
// ------------------------------

import br.com.louise.AppProdutos.model.RefreshTokenEntity;
import br.com.louise.AppProdutos.service.TokenService;
import br.com.louise.AppProdutos.service.AppUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "01. Autenticação e Usuários")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AppUserDetailsService appUserDetailsService;

    @Operation(summary = "Realizar Login", description = "Autentica o usuário e retorna os tokens de acesso (Access Token) e renovação (Refresh Token).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
            @ApiResponse(responseCode = "403", description = "Email ou senha inválidos")
    })
    @PostMapping("/login")
    public ResponseEntity<DTOAuthResponse> login(@RequestBody DTOAuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        String accessToken = tokenService.generateAccessToken(userDetails);
        RefreshTokenEntity refreshToken = tokenService.createRefreshToken(userDetails.getUsername());

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(DTOAuthResponse.builder()
                .email(userDetails.getUsername())
                .role(role)
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build());
    }

    @Operation(summary = "Renovar Token", description = "Usa o Refresh Token para gerar um novo Access Token sem precisar logar novamente.")
    @PostMapping("/refresh")
    public ResponseEntity<DTOAuthResponse> refreshToken(@RequestBody DTORefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return tokenService.findByToken(requestRefreshToken)
                .map(tokenService::verifyExpiration)
                .map(RefreshTokenEntity::getUser)
                .map(user -> {
                    UserDetails userDetails = appUserDetailsService.loadUserByUsername(user.getEmail());
                    String token = tokenService.generateAccessToken(userDetails);

                    return ResponseEntity.ok(DTOAuthResponse.builder()
                            .accessToken(token)
                            .refreshToken(requestRefreshToken)
                            .email(user.getEmail())
                            .role(user.getRole())
                            .build());
                })
                .orElseThrow(() -> new RuntimeException("Refresh token não encontrado ou inválido!"));
    }

    @GetMapping("/me")
    public String getMe(Authentication authentication) {
        return "Usuário autenticado: " + authentication.getName();
    }
}