package br.com.louise.AppProdutos.dto.order;

import br.com.louise.AppProdutos.dto.payment.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DTOOrderRequest {

    // Informações opcionais de contato (caso sejam diferentes do cadastro do usuário)
    private String customerName;
    private String phoneNumber;

    // A única informação obrigatória para fechar o pedido agora
    private PaymentMethod paymentMethod;
    private String couponCode;
}