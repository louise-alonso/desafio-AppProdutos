package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.coupon.DTOCouponRequest;
import br.com.louise.AppProdutos.model.CouponEntity;
import br.com.louise.AppProdutos.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
@Tag(name = "04. Cupons", description = "Gestão de promoções e códigos de desconto")
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Criar cupom de desconto", description = "Cria um novo código promocional (Percentual ou Fixo) com regras de validade e limites de uso.")
    public CouponEntity createCoupon(@RequestBody DTOCouponRequest request) {
        return couponService.createCoupon(request);
    }
}