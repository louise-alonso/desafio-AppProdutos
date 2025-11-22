package br.com.louise.AppProdutos.config;

import br.com.louise.AppProdutos.filters.ValidFilterJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final ValidFilterJWT validFilterJWT;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ====================================================
                        // 1. REGRAS PÚBLICAS (DEVEM VIR PRIMEIRO)
                        // ====================================================

                        // Cadastro (Ovo e a Galinha) - TEM QUE SER A PRIMEIRA
                        .requestMatchers(HttpMethod.POST, "/admin/register").permitAll()

                        // Autenticação
                        .requestMatchers("/auth/**").permitAll()

                        // H2 e Swagger/Docs
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Catálogo Público (GET)
                        .requestMatchers(HttpMethod.GET, "/products/**", "/categories/**").permitAll()

                        // ====================================================
                        // 2. REGRAS PROTEGIDAS (VÊM DEPOIS)
                        // ====================================================

                        // Qualquer outra rota /admin/** que não seja o register acima, precisa ser ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // O restante exige autenticação genérica
                        .anyRequest().authenticated()
                )
                .addFilterBefore(validFilterJWT, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}