package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.DTOAuthRequest;
import br.com.louise.AppProdutos.dto.DTOAuthResponse;
import br.com.louise.AppProdutos.service.impl.AppUserDetailsService;
import br.com.louise.AppProdutos.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AppUserDetailsService appUserDetailsService;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public DTOAuthResponse login(@RequestBody DTOAuthRequest request) throws Exception {
        // autenticação email e senha
        authenticate(request.getEmail(), request.getPassword());
        final UserDetails userDetails = appUserDetailsService.loadUserByUsername(request.getEmail());

        final String token = jwtUtil.generateToken(userDetails);
        final String role = userDetails.getAuthorities().iterator().next().getAuthority();

        return DTOAuthResponse.builder()
                .email(userDetails.getUsername())
                .role(role)
                .token(token)
                .build();
    }

    private void authenticate(String email, String password) throws Exception {
        try {
            // verifica se as credenciais batem
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new DisabledException("Usuário desabilitado", e);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Credenciais inválidas", e);
        }
    }

    @PostMapping("/encode")
    public String encodePassword(@RequestBody Map<String, String> request) {
        return passwordEncoder.encode(request.get("password"));
    }
}