package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.DTOAuthRequest;
import br.com.louise.AppProdutos.dto.DTOAuthResponse;
import br.com.louise.AppProdutos.dto.DTORefreshTokenRequest;
import br.com.louise.AppProdutos.model.RefreshTokenEntity;
import br.com.louise.AppProdutos.service.TokenService;
import br.com.louise.AppProdutos.service.impl.AppUserDetailsService;
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
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final AppUserDetailsService appUserDetailsService;

    @PostMapping("/login")
    public ResponseEntity<DTOAuthResponse> login(@RequestBody DTOAuthRequest request) {
        // 1. Autentica
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        // 2. Gera Access Token
        String accessToken = tokenService.generateAccessToken(userDetails);

        // 3. Gera Refresh Token
        RefreshTokenEntity refreshToken = tokenService.createRefreshToken(userDetails.getUsername());

        String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return ResponseEntity.ok(DTOAuthResponse.builder()
                .email(userDetails.getUsername())
                .role(role)
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<DTOAuthResponse> refreshToken(@RequestBody DTORefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return tokenService.findByToken(requestRefreshToken)
                .map(tokenService::verifyExpiration) // Verifica se venceu
                .map(RefreshTokenEntity::getUser)    // Pega o usuário dono do token
                .map(user -> {
                    // Carrega UserDetails
                    UserDetails userDetails = appUserDetailsService.loadUserByUsername(user.getEmail());

                    // Gera NOVO Access Token
                    String token = tokenService.generateAccessToken(userDetails);

                    return ResponseEntity.ok(DTOAuthResponse.builder()
                            .accessToken(token)
                            .refreshToken(requestRefreshToken) // Mantém o mesmo refresh (ou poderia girar)
                            .email(user.getEmail())
                            .role(user.getRole()) // Assumindo que a role está salva assim ou precisa de prefixo
                            .build());
                })
                .orElseThrow(() -> new RuntimeException("Refresh token não encontrado no banco!"));
    }
}