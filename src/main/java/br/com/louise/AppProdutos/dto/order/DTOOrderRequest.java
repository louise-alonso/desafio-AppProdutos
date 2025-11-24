package br.com.louise.AppProdutos.dto.order;

import br.com.louise.AppProdutos.dto.payment.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DTOOrderRequest {

    @Schema(description = "Nome no pedido (opcional)", example = "João da Silva")
    private String customerName;

    @Schema(description = "Telefone de contato", example = "11999998888")
    private String phoneNumber;

    @Schema(description = "Método de Pagamento", example = "PIX")
    private PaymentMethod paymentMethod;

    @Schema(description = "Código do cupom (opcional)", example = "NATAL10")
    private String couponCode;
}