// service/ProductPermissionService.java (NOVO ARQUIVO)

package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("productPermissionService") // O nome do Bean é crucial para o @PreAuthorize
@RequiredArgsConstructor
public class ProductPermissionService {

    private final ProductRepository productRepository;

    /**
     * Verifica se o usuário logado é o proprietário do produto.
     * @param productId O ID do produto a ser verificado (Path Variable).
     * @return true se for o proprietário, false caso contrário.
     */
    public boolean isOwner(String productId) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<ProductEntity> productOpt = productRepository.findByProductId(productId);

        if (productOpt.isEmpty()) {
            return false;
        }

        ProductEntity product = productOpt.get();

        // Compara o email do usuário logado com o email do proprietário do produto
        return product.getOwner().getEmail().equals(currentEmail);
    }
}