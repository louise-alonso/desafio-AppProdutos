package br.com.louise.AppProdutos.dto.cart;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DTOCartResponse {
    private Long cartId;
    private BigDecimal totalAmount;
    private List<DTOCartItemResponse> items;

    @Data
    @Builder
    public static class DTOCartItemResponse {
        private Long itemId;
        private String productId;
        private String productName;
        private BigDecimal unitPrice;
        private Integer quantity;
        private BigDecimal subTotal;
    }
}