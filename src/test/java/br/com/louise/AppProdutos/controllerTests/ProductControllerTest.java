package br.com.louise.AppProdutos.controllerTests;

import br.com.louise.AppProdutos.controller.ProductController;
import br.com.louise.AppProdutos.dto.product.DTOProductRequest;
import br.com.louise.AppProdutos.dto.product.DTOProductResponse;
import br.com.louise.AppProdutos.security.AppUserDetailsService;
import br.com.louise.AppProdutos.security.TokenService;
import br.com.louise.AppProdutos.service.ProductService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ProductService productService;
    @MockBean private TokenService tokenService;
    @MockBean private AppUserDetailsService appUserDetailsService;

    @Test
    @WithMockUser(roles = "SELLER")
    void addProduct_ShouldReturnCreated() throws Exception {
        DTOProductRequest request = DTOProductRequest.builder()
                .name("Notebook")
                .price(BigDecimal.valueOf(3000))
                .categoryId("cat-1")
                .stockQuantity(10)
                .sku("NOTE-001")
                .build();

        DTOProductResponse response = new DTOProductResponse();
        response.setProductId("prod-1");
        response.setName("Notebook");

        when(productService.add(any())).thenReturn(response);

        mockMvc.perform(post("/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value("prod-1"));
    }

    @Test
    void readProducts_ShouldReturnList() throws Exception {
        when(productService.fetchProducts()).thenReturn(List.of(new DTOProductResponse()));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }
}