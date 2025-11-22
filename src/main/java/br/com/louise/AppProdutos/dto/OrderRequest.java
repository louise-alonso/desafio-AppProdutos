package br.com.louise.AppProdutos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderRequest {
    private String customerName;
    private String phoneNumber;
    private PaymentMethod paymentMethod;
    private List<OrderProductRequest> cartProducts;
    private Double subTotal;
    private Double tax;
    private Double grandTotal;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OrderProductRequest {
        private String productId;
        private Integer quantity;
    }
}