package br.com.louise.AppProdutos.security;

import br.com.louise.AppProdutos.config.Constants;
import br.com.louise.AppProdutos.model.RefreshTokenEntity;
import br.com.louise.AppProdutos.model.UserEntity;
import br.com.louise.AppProdutos.repository.RefreshTokenRepository;
import br.com.louise.AppProdutos.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    public String generateAccessToken(UserDetails userDetails) {
        return JWT.create()
                .withSubject(userDetails.getUsername())
                .withClaim("role", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .withExpiresAt(new Date(System.currentTimeMillis() + Constants.TOKEN_EXPIRATION))
                .sign(Algorithm.HMAC512(Constants.TOKEN_PASSWORD));
    }

    public RefreshTokenEntity createRefreshToken(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado para Refresh Token"));

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .user(user)
                .token(UUID.randomUUID().toString()) // Token opaco (não é JWT)
                .expiryDate(Instant.now().plusMillis(Constants.REFRESH_EXPIRATION))
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expirado. Faça login novamente.");
        }
        return token;
    }

    public Optional<RefreshTokenEntity> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public String validateTokenAndGetSubject(String token) {
        try {
            return JWT.require(Algorithm.HMAC512(Constants.TOKEN_PASSWORD))
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}