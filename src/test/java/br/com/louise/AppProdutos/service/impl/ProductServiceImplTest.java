package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.product.DTOProductRequest;
import br.com.louise.AppProdutos.dto.product.DTOProductResponse;
import br.com.louise.AppProdutos.model.CategoryEntity;
import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.model.UserEntity;
import br.com.louise.AppProdutos.repository.CategoryRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.repository.UserRepository;
import br.com.louise.AppProdutos.service.AuditService;
import br.com.louise.AppProdutos.service.impl.ProductServiceImpl;
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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @InjectMocks private ProductServiceImpl productService;

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private UserRepository userRepository;
    @Mock private AuditService auditService; // <--- CORREÇÃO: Mock adicionado

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    private ProductEntity product;
    private UserEntity owner;

    @BeforeEach
    void setup() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getName()).thenReturn("vendedor@teste.com");

        owner = UserEntity.builder().email("vendedor@teste.com").role("SELLER").build();
        product = ProductEntity.builder()
                .productId("prod-1")
                .name("Antigo")
                .sku("SKU-1")
                .price(BigDecimal.TEN)
                .owner(owner)
                .build();
    }

    @Test
    void updateProduct_ShouldSucceed_WhenSellerIsOwner() {
        DTOProductRequest request = DTOProductRequest.builder()
                .name("Novo Nome")
                .price(BigDecimal.valueOf(20))
                .sku("SKU-1")
                .stockQuantity(10)
                .build();

        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_SELLER")))
                .when(authentication).getAuthorities();

        when(productRepository.findByProductId("prod-1")).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        DTOProductResponse response = productService.updateProduct("prod-1", request);

        assertEquals("Novo Nome", response.getName());
        // Verifica se a auditoria foi chamada
        verify(auditService).log(eq("UPDATE"), eq("Product"), any(), any(), any());
    }
}