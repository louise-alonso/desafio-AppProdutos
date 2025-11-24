package br.com.louise.AppProdutos.dto.order;

import br.com.louise.AppProdutos.dto.payment.DTOPaymentDetails;
import br.com.louise.AppProdutos.dto.payment.PaymentMethod;
import br.com.louise.AppProdutos.model.OrderStatus; // Importar o novo enum
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private String orderId;
    private String customerEmail;
    private OrderStatus status;
    private String customerName;
    private String phoneNumber;

    private BigDecimal subTotal;
    private BigDecimal tax;
    private BigDecimal grandTotal;

    private PaymentMethod paymentMethod;
    private LocalDateTime createdAt;
    private DTOPaymentDetails paymentDetails;
    private List<OrderProductResponse> products;


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OrderProductResponse {
        private String productId;
        private String name;
        private BigDecimal price;
        private Integer quantity;
    }
}