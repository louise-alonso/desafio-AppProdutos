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
    @Mock private AuditService auditService;

    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    private ProductEntity product;
    private UserEntity owner;
    private CategoryEntity category;

    @BeforeEach
    void setup() {
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);

        lenient().when(authentication.getName()).thenReturn("vendedor@teste.com");

        owner = UserEntity.builder().email("vendedor@teste.com").role("SELLER").build();
        category = CategoryEntity.builder().categoryId("cat-1").name("Geral").build();

        product = ProductEntity.builder()
                .productId("prod-1")
                .name("Antigo")
                .sku("SKU-ANTIGO")
                .price(BigDecimal.TEN)
                .owner(owner)
                .category(category)
                .build();
    }

    @Test
    void add_ShouldCreateProduct_WhenValid() {
        DTOProductRequest request = DTOProductRequest.builder()
                .name("Novo Prod")
                .sku("SKU-NOVO")
                .price(BigDecimal.TEN)
                .categoryId("cat-1")
                .stockQuantity(5)
                .build();

        when(productRepository.existsBySku("SKU-NOVO")).thenReturn(false);
        when(categoryRepository.findByCategoryId("cat-1")).thenReturn(Optional.of(category));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(owner));
        when(productRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        DTOProductResponse response = productService.add(request);

        assertNotNull(response);
        assertEquals("Novo Prod", response.getName());
        verify(auditService).log(eq("CREATE"), eq("Product"), any(), isNull(), any());
    }

    @Test
    void add_ShouldThrowException_WhenSkuExists() {
        DTOProductRequest request = DTOProductRequest.builder().sku("SKU-EXISTENTE").build();
        when(productRepository.existsBySku("SKU-EXISTENTE")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> productService.add(request));
    }

    @Test
    void updateProduct_ShouldSucceed_WhenSellerIsOwner() {
        DTOProductRequest request = DTOProductRequest.builder()
                .name("Novo Nome")
                .price(BigDecimal.valueOf(20))
                .sku("SKU-ANTIGO")
                .stockQuantity(10)
                .build();

        // Mock de permissão SELLER
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_SELLER")))
                .when(authentication).getAuthorities();

        when(productRepository.findByProductId("prod-1")).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        DTOProductResponse response = productService.updateProduct("prod-1", request);

        assertEquals("Novo Nome", response.getName());
        verify(auditService).log(eq("UPDATE"), eq("Product"), any(), any(), any());
    }

    @Test
    void deleteProducts_ShouldDelete_WhenExists() {
        when(productRepository.findByProductId("prod-1")).thenReturn(Optional.of(product));

        productService.deleteProducts("prod-1");

        verify(productRepository).delete(product);
        verify(auditService).log(eq("DELETE"), eq("Product"), eq("prod-1"), any(), isNull());
    }

    @Test
    void readProductById_ShouldThrowException_WhenNotFound() {
        when(productRepository.findByProductId("inexistente")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> productService.readProductById("inexistente"));
    }
}