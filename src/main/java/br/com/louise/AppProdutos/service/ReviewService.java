package br.com.louise.AppProdutos.service;

import br.com.louise.AppProdutos.dto.review.DTOReviewRequest;
import br.com.louise.AppProdutos.dto.review.DTOReviewResponse;

import java.util.List;

public interface ReviewService {
    DTOReviewResponse createReview(DTOReviewRequest request);
    List<DTOReviewResponse> getProductReviews(String productId);
}