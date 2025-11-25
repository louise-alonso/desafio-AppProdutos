package br.com.louise.AppProdutos.controllerTests;

import br.com.louise.AppProdutos.controller.CartController;
import br.com.louise.AppProdutos.dto.cart.DTOCartItemRequest;
import br.com.louise.AppProdutos.dto.cart.DTOCartResponse;
import br.com.louise.AppProdutos.security.AppUserDetailsService;
import br.com.louise.AppProdutos.security.TokenService;
import br.com.louise.AppProdutos.service.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc(addFilters = false)
class CartControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CartService cartService;
    @MockBean private TokenService tokenService;
    @MockBean private AppUserDetailsService appUserDetailsService;

    @Test
    @WithMockUser(username = "user@test.com")
    void getCart_ShouldReturnCart() throws Exception {
        // ARRANGE: Cria o objeto de resposta usando o Builder
        DTOCartResponse response = DTOCartResponse.builder()
                .totalAmount(BigDecimal.TEN)
                .items(Collections.emptyList())
                .build();

        when(cartService.getMyCart()).thenReturn(response);

        // ACT & ASSERT
        mockMvc.perform(get("/cart"))
                .andExpect(status().isOk())
                // Valida o campo 'totalAmount' (nome exato da variável no DTO)
                .andExpect(jsonPath("$.totalAmount").value(10));
    }

    @Test
    @WithMockUser
    void addItem_ShouldReturnUpdatedCart() throws Exception {
        DTOCartItemRequest request = new DTOCartItemRequest();
        request.setProductId("prod-1");
        request.setQuantity(1);

        mockMvc.perform(post("/cart/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(cartService).addItemToCart(any());
    }

    @Test
    @WithMockUser
    void clearCart_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/cart/clear"))
                .andExpect(status().isOk());

        verify(cartService).clearCart();
    }
}