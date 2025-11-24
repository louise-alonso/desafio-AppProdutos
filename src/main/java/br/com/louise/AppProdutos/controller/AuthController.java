package br.com.louise.AppProdutos.controller;

// --- NOVOS IMPORTS CORRETOS ---
import br.com.louise.AppProdutos.dto.auth.DTOAuthRequest;
import br.com.louise.AppProdutos.dto.auth.DTOAuthResponse;
import br.com.louise.AppProdutos.dto.auth.DTORefreshTokenRequest;
// ------------------------------

import br.com.louise.AppProdutos.model.RefreshTokenEntity;
import br.com.louise.AppProdutos.service.TokenService;
import br.com.louise.AppProdutos.service.AppUserDetailsService;
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