package br.com.louise.AppProdutos.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DTOPaymentDetails {

    @Schema(description = "ID do pedido no Gateway (Razorpay/Stripe)", example = "order_Nkasd8923n")
    private String razorpayOrderId;

    @Schema(description = "ID do pagamento confirmado", example = "pay_298392")
    private String razorpayPaymentId;

    @Schema(description = "Assinatura digital para validação", example = "b2a9e8...")
    private String razorpaySignature;

    @Schema(description = "Status atual do pagamento", example = "PAID")
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    public enum PaymentStatus {
        CREATED,
        PAID,
        SHIPPED,
        DELIVERED,
        CANCELLED
    }
}