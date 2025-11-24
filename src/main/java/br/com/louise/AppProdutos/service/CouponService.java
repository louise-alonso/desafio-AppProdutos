package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.dto.coupon.DTOCouponRequest;
import br.com.louise.AppProdutos.model.CouponEntity;
import br.com.louise.AppProdutos.model.OrderProductEntity;
import br.com.louise.AppProdutos.model.UserEntity;

import java.math.BigDecimal;
import java.util.List;

public interface CouponService {

    // Cria um novo cupom
    CouponEntity createCoupon(DTOCouponRequest request);

    // Valida regras e calcula o valor monetário do desconto
    BigDecimal validateAndCalculateDiscount(String code, UserEntity user, List<OrderProductEntity> products, BigDecimal orderSubTotal);

    // Incrementa o contador de uso após fechar o pedido
    void incrementGlobalUsage(String code);
}