package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.coupon.DTOCouponRequest;
import br.com.louise.AppProdutos.model.CouponEntity;
import br.com.louise.AppProdutos.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/coupons")
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CouponEntity createCoupon(@RequestBody DTOCouponRequest request) {
        return couponService.createCoupon(request);
    }
}