package br.com.louise.AppProdutos.controller;

import br.com.louise.AppProdutos.dto.review.DTOReviewRequest;
import br.com.louise.AppProdutos.dto.review.DTOReviewResponse;
import br.com.louise.AppProdutos.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "08. Avaliações (Reviews)", description = "Feedback de clientes e classificação de produtos")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Avaliar produto", description = "Regista uma avaliação (nota 1-5). Regra: O utilizador SÓ pode avaliar se tiver comprado o produto e o pedido estiver pago/entregue.")
    public DTOReviewResponse createReview(@RequestBody @Valid DTOReviewRequest request) {
        return reviewService.createReview(request);
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Listar avaliações", description = "Mostra todos os comentários e notas de um produto específico. Acesso público.")
    public List<DTOReviewResponse> getProductReviews(@PathVariable String productId) {
        return reviewService.getProductReviews(productId);
    }
}