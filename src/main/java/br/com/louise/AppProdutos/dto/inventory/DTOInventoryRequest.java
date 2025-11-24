package br.com.louise.AppProdutos.dto;

import br.com.louise.AppProdutos.model.TransactionType;
import lombok.Data;

@Data
public class DTOInventoryRequest {
    private String productId;
    private Integer quantity;
    private TransactionType type; // ENTRY, EXIT, ADJUSTMENT
    private String description;
}