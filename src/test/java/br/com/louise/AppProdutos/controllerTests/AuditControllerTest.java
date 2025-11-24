package br.com.louise.AppProdutos.controllerTests;

import br.com.louise.AppProdutos.controller.AuditController;
import br.com.louise.AppProdutos.model.AuditLogEntity;
import br.com.louise.AppProdutos.service.AuditService;
import br.com.louise.AppProdutos.service.TokenService;
import br.com.louise.AppProdutos.service.AppUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuditControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private AuditService auditService;

    // Mocks de segurança obrigatórios
    @MockBean private TokenService tokenService;
    @MockBean private AppUserDetailsService appUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAuditLogs_ShouldReturnOk_WhenAdmin() throws Exception {
        when(auditService.getLogsByEntity(anyString())).thenReturn(List.of(new AuditLogEntity()));

        mockMvc.perform(get("/audit?entity=Product"))
                .andExpect(status().isOk());
    }

    // Nota: O teste de 403 Forbidden real requer 'addFilters = true',
    // mas aqui validamos a existência do endpoint e contrato.
}