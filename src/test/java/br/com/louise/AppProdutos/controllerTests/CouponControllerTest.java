package br.com.louise.AppProdutos.controllerTests;

import br.com.louise.AppProdutos.controller.CouponController;
import br.com.louise.AppProdutos.dto.coupon.DTOCouponRequest;
import br.com.louise.AppProdutos.model.CouponEntity;
import br.com.louise.AppProdutos.model.DiscountType;
import br.com.louise.AppProdutos.security.AppUserDetailsService;
import br.com.louise.AppProdutos.security.TokenService;
import br.com.louise.AppProdutos.service.CouponService;
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
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CouponController.class)
@AutoConfigureMockMvc(addFilters = false)
class CouponControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CouponService couponService;
    @MockBean private TokenService tokenService;
    @MockBean private AppUserDetailsService appUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCoupon_ShouldReturnCreated() throws Exception {
        DTOCouponRequest request = new DTOCouponRequest();
        request.setCode("TEST10");
        request.setType(DiscountType.PERCENTAGE);
        request.setValue(BigDecimal.TEN);
        request.setExpirationDate(LocalDate.now().plusDays(1));

        CouponEntity response = new CouponEntity();
        response.setCode("TEST10");

        when(couponService.createCoupon(any())).thenReturn(response);

        mockMvc.perform(post("/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("TEST10"));
    }
}