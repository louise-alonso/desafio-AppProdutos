package br.com.louise.AppProdutos.controllerTests;

import br.com.louise.AppProdutos.controller.AuthController;
import br.com.louise.AppProdutos.dto.auth.DTOAuthRequest;
import br.com.louise.AppProdutos.dto.auth.DTOAuthResponse;
import br.com.louise.AppProdutos.model.RefreshTokenEntity;
import br.com.louise.AppProdutos.model.UserEntity;
import br.com.louise.AppProdutos.security.AppUserDetailsService;
import br.com.louise.AppProdutos.security.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private AuthenticationManager authenticationManager;
    @MockBean private TokenService tokenService;
    @MockBean private AppUserDetailsService appUserDetailsService;

    @Test
    void login_ShouldReturnTokens_WhenCredentialsAreValid() throws Exception {
        // Arrange
        DTOAuthRequest loginRequest = new DTOAuthRequest("user@test.com", "123456");

        // Mock do UserDetails e Authentication
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "user@test.com", "encodedPass", Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        Authentication auth = mock(Authentication.class);
        when(auth.getPrincipal()).thenReturn(userDetails);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(tokenService.generateAccessToken(any())).thenReturn("access-token-fake");

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder().token("refresh-token-fake").build();
        when(tokenService.createRefreshToken(any())).thenReturn(refreshToken);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-token-fake"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token-fake"));
    }
}