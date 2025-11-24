package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.inventory.DTOInventoryRequest;
import br.com.louise.AppProdutos.model.InventoryTransactionEntity;
import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.repository.InventoryTransactionRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.service.InventoryService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryTransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void processTransaction(DTOInventoryRequest request, String responsibleEmail) {

        // 1. Busca o Produto
        ProductEntity product = productRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado: " + request.getProductId()));

        // --- SEGURANÇA: Verificar se o SELLER é dono do produto ---
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        if ("ROLE_SELLER".equals(role)) {
            if (!product.getOwner().getEmail().equals(responsibleEmail)) {
                throw new AccessDeniedException("Você não tem permissão para alterar o estoque deste produto.");
            }
        }
        // -----------------------------------------------------------

        // 2. Calcula o novo estoque
        int currentStock = product.getStockQuantity();
        int quantityToMove = request.getQuantity();
        int newStock = currentStock;

        switch (request.getType()) {
            case ENTRY:
            case RETURN:
            case ADJUSTMENT: // Aqui assumimos ajuste positivo. Para reduzir, usar EXIT.
                newStock = currentStock + quantityToMove;
                break;
            case EXIT:
                if (currentStock < quantityToMove) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estoque insuficiente. Atual: " + currentStock);
                }
                newStock = currentStock - quantityToMove;
                break;
        }

        // 3. Atualiza o Produto
        product.setStockQuantity(newStock);
        productRepository.save(product);

        // 4. Registra a Transação (Log)
        InventoryTransactionEntity transaction = InventoryTransactionEntity.builder()
                .product(product)
                .quantity(quantityToMove)
                .type(request.getType())
                .description(request.getDescription())
                .responsibleEmail(responsibleEmail)
                .build();

        transactionRepository.save(transaction);
    }

    @Override
    public List<InventoryTransactionEntity> getHistoryByProduct(String productId) {
        return transactionRepository.findByProductProductIdOrderByCreatedAtDesc(productId);
    }
}