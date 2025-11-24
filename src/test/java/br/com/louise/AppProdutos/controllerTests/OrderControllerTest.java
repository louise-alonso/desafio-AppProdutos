package br.com.louise.AppProdutos.controllerTests;
import br.com.louise.AppProdutos.controller.OrderController;
import br.com.louise.AppProdutos.dto.order.DTOOrderRequest;
import br.com.louise.AppProdutos.dto.order.DTOOrderResponse;
import br.com.louise.AppProdutos.dto.payment.PaymentMethod;
import br.com.louise.AppProdutos.model.OrderStatus;
import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import br.com.louise.AppProdutos.repository.RefreshTokenRepository;
import br.com.louise.AppProdutos.service.OrderPermissionService;
import br.com.louise.AppProdutos.service.OrderService;
import br.com.louise.AppProdutos.service.TokenService;
import br.com.louise.AppProdutos.service.AppUserDetailsService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private OrderService orderService;
    @MockBean private OrderPermissionService orderPermissionService;
    @MockBean private TokenService tokenService;
    @MockBean private AppUserDetailsService appUserDetailsService;
    @MockBean private RefreshTokenRepository refreshTokenRepository;

    @MockBean private OrderEntityRepository orderRepository; // <--- CORREÇÃO: Mock adicionado

    @Test
    @WithMockUser(username = "cliente", roles = "CUSTOMER")
    void createOrder_ShouldReturn201_WhenRequestIsValid() throws Exception {
        DTOOrderRequest request = DTOOrderRequest.builder()
                .paymentMethod(PaymentMethod.PIX)
                .phoneNumber("11999999999")
                .build();

        DTOOrderResponse responseFake = DTOOrderResponse.builder()
                .orderId("ord-123")
                .status(OrderStatus.PAID)
                .grandTotal(new BigDecimal("500.00"))
                .build();

        when(orderService.createOrder(any(DTOOrderRequest.class))).thenReturn(responseFake);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("ord-123"));
    }
}