package br.com.louise.AppProdutos.controllerTests;

import br.com.louise.AppProdutos.controller.ReportController;
import br.com.louise.AppProdutos.dto.report.DTOSalesReport;
import br.com.louise.AppProdutos.service.ReportService;
import br.com.louise.AppProdutos.security.TokenService;
import br.com.louise.AppProdutos.security.AppUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReportControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private ReportService reportService;
    @MockBean private TokenService tokenService;
    @MockBean private AppUserDetailsService appUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getSalesReport_ShouldReturnOk() throws Exception {
        DTOSalesReport reportMock = new DTOSalesReport(LocalDate.now(), 10L, new BigDecimal("500.00"));

        when(reportService.getSalesReport(any(), any())).thenReturn(List.of(reportMock));

        mockMvc.perform(get("/reports/sales")
                        .param("start", "2023-01-01")
                        .param("end", "2023-01-31"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getTopProducts_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/reports/top-products"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getLowStock_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/reports/low-stock").param("min", "5"))
                .andExpect(status().isOk());
    }
}