package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.model.AuditLogEntity;
import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.repository.AuditRepository;
import br.com.louise.AppProdutos.service.impl.AuditServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @InjectMocks
    private AuditServiceImpl auditService;

    @Mock private AuditRepository auditRepository;
    @Mock private ObjectMapper objectMapper;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @Captor
    private ArgumentCaptor<AuditLogEntity> auditCaptor;

    @Test
    void log_ShouldSaveAuditEntity_WhenCalled() throws JsonProcessingException {
        // Arrange
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("admin@test.com");

        ProductEntity oldProd = ProductEntity.builder().productId("1").name("Old").build();
        ProductEntity newProd = ProductEntity.builder().productId("1").name("New").build();

        when(objectMapper.writeValueAsString(oldProd)).thenReturn("{\"name\":\"Old\"}");
        when(objectMapper.writeValueAsString(newProd)).thenReturn("{\"name\":\"New\"}");

        // Act
        auditService.log("UPDATE", "Product", "1", oldProd, newProd);

        // Assert
        verify(auditRepository).save(auditCaptor.capture());
        AuditLogEntity savedLog = auditCaptor.getValue();

        assertEquals("UPDATE", savedLog.getAction());
        assertEquals("Product", savedLog.getEntityName());
        assertEquals("{\"name\":\"Old\"}", savedLog.getOldState());
        assertEquals("{\"name\":\"New\"}", savedLog.getNewState());
        assertEquals("admin@test.com", savedLog.getChangedBy());
    }

    @Test
    void log_ShouldNotThrowException_WhenSerializationFails() throws JsonProcessingException {
        when(objectMapper.writeValueAsString(any())).thenThrow(new RuntimeException("Json Error"));

        assertDoesNotThrow(() ->
                auditService.log("ERROR", "Test", "1", new Object(), null)
        );

        verify(auditRepository, never()).save(any());
    }
}