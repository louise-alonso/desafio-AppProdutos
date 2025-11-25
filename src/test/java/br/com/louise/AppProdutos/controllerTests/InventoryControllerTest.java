package br.com.louise.AppProdutos.controllerTests;

import br.com.louise.AppProdutos.controller.InventoryController;
import br.com.louise.AppProdutos.dto.inventory.DTOInventoryRequest;
import br.com.louise.AppProdutos.model.TransactionType;
import br.com.louise.AppProdutos.security.AppUserDetailsService;
import br.com.louise.AppProdutos.security.TokenService;
import br.com.louise.AppProdutos.service.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
class InventoryControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private InventoryService inventoryService;
    @MockBean private TokenService tokenService;
    @MockBean private AppUserDetailsService appUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void adjustInventory_ShouldReturnOk() throws Exception {
        DTOInventoryRequest request = new DTOInventoryRequest();
        request.setProductId("prod-1");
        request.setQuantity(10);
        request.setType(TransactionType.ENTRY);

        mockMvc.perform(post("/inventory/adjust")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(inventoryService).processTransaction(any(), anyString());
    }
}