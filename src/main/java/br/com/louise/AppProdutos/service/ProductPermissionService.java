package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("productPermissionService") // O nome aqui deve bater com o do @PreAuthorize
@RequiredArgsConstructor
public class ProductPermissionService {

    private final ProductRepository productRepository;

    public boolean isOwner(String productId) {
        // 1. Pega quem está logado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String emailLogado = authentication.getName();

        // 2. Busca o produto e verifica o dono
        return productRepository.findByProductId(productId)
                .map(product -> product.getOwner() != null && product.getOwner().getEmail().equals(emailLogado))
                .orElse(false);
    }
}