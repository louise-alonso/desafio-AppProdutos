package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.review.DTOReviewRequest;
import br.com.louise.AppProdutos.dto.review.DTOReviewResponse;
import br.com.louise.AppProdutos.service.ReviewService;
import jakarta.validation.Valid; // <--- Import
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    public DTOReviewResponse createReview(@RequestBody @Valid DTOReviewRequest request) { // <--- @Valid
        return reviewService.createReview(request);
    }

    @GetMapping("/product/{productId}")
    public List<DTOReviewResponse> getProductReviews(@PathVariable String productId) {
        return reviewService.getProductReviews(productId);
    }
}