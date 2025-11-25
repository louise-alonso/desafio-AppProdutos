package br.com.louise.AppProdutos.controllerTests;

import br.com.louise.AppProdutos.controller.CategoryController;
import br.com.louise.AppProdutos.dto.category.DTOCategoryRequest;
import br.com.louise.AppProdutos.dto.category.DTOCategoryResponse;
import br.com.louise.AppProdutos.security.AppUserDetailsService;
import br.com.louise.AppProdutos.security.TokenService;
import br.com.louise.AppProdutos.service.CategoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private CategoryService categoryService;
    @MockBean private TokenService tokenService;
    @MockBean private AppUserDetailsService appUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void addCategory_ShouldReturnCreated() throws Exception {
        DTOCategoryRequest request = new DTOCategoryRequest();
        request.setName("Eletrônicos");

        DTOCategoryResponse response = DTOCategoryResponse.builder()
                .categoryId("cat-1")
                .name("Eletrônicos")
                .build();

        when(categoryService.add(any())).thenReturn(response);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryId").value("cat-1"));
    }

    @Test
    void fetchCategories_ShouldReturnList() throws Exception {
        when(categoryService.read()).thenReturn(List.of(DTOCategoryResponse.builder().build()));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}