package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.inventory.DTOInventoryRequest;
import br.com.louise.AppProdutos.model.InventoryTransactionEntity;
import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.model.TransactionType;
import br.com.louise.AppProdutos.repository.InventoryTransactionRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.service.impl.InventoryServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @InjectMocks private InventoryServiceImpl inventoryService;
    @Mock private InventoryTransactionRepository transactionRepository;
    @Mock private ProductRepository productRepository;

    @Mock private SecurityContext securityContext; // <--- CORREÇÃO
    @Mock private Authentication authentication;   // <--- CORREÇÃO

    private ProductEntity product;

    @BeforeEach
    void setup() {
        product = ProductEntity.builder().productId("prod-uuid-1").name("Mouse").stockQuantity(10).build();

        // --- CONFIGURAÇÃO DE SEGURANÇA CORRIGIDA ---
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        // Simula ROLE_ADMIN para passar na validação
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();
    }

    @Test
    void processTransaction_ShouldIncreaseStock_WhenTypeIsEntry() {
        DTOInventoryRequest request = new DTOInventoryRequest();
        request.setProductId("prod-uuid-1");
        request.setQuantity(5);
        request.setType(TransactionType.ENTRY);

        when(productRepository.findByProductId("prod-uuid-1")).thenReturn(Optional.of(product));

        inventoryService.processTransaction(request, "admin@teste.com");

        assertEquals(15, product.getStockQuantity());
        verify(transactionRepository).save(any(InventoryTransactionEntity.class));
    }

    @Test
    void processTransaction_ShouldDecreaseStock_WhenTypeIsExit() {
        DTOInventoryRequest request = new DTOInventoryRequest();
        request.setProductId("prod-uuid-1");
        request.setQuantity(3);
        request.setType(TransactionType.EXIT);

        when(productRepository.findByProductId("prod-uuid-1")).thenReturn(Optional.of(product));

        inventoryService.processTransaction(request, "cliente@teste.com");

        assertEquals(7, product.getStockQuantity());
    }

    @Test
    void processTransaction_ShouldThrowError_WhenStockInsufficient() {
        DTOInventoryRequest request = new DTOInventoryRequest();
        request.setProductId("prod-uuid-1");
        request.setQuantity(20);
        request.setType(TransactionType.EXIT);

        when(productRepository.findByProductId("prod-uuid-1")).thenReturn(Optional.of(product));

        assertThrows(ResponseStatusException.class, () ->
                inventoryService.processTransaction(request, "cliente@teste.com")
        );
    }
}