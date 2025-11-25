package br.com.louise.AppProdutos.service.impl;

import br.com.louise.AppProdutos.dto.review.DTOReviewRequest;
import br.com.louise.AppProdutos.dto.review.DTOReviewResponse;
import br.com.louise.AppProdutos.model.OrderEntity;
import br.com.louise.AppProdutos.model.OrderStatus;
import br.com.louise.AppProdutos.model.ProductEntity;
import br.com.louise.AppProdutos.model.ReviewEntity;
import br.com.louise.AppProdutos.model.UserEntity;
import br.com.louise.AppProdutos.repository.OrderEntityRepository;
import br.com.louise.AppProdutos.repository.ProductRepository;
import br.com.louise.AppProdutos.repository.ReviewRepository;
import br.com.louise.AppProdutos.repository.UserRepository;
import br.com.louise.AppProdutos.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderEntityRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DTOReviewResponse createReview(DTOReviewRequest request) {
        // 1. Quem está avaliando?
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        // 2. Valida Nota (1-5)
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A nota deve ser entre 1 e 5.");
        }

        // 3. Valida se o Produto existe
        ProductEntity product = productRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

        // 4. Valida se o Pedido existe, é do usuário e tem o produto
        OrderEntity order = orderRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado"));

        if (!order.getCustomer().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Este pedido não pertence a você.");
        }

        // Verifica Status (Só pode avaliar se já pagou ou recebeu)
        if (order.getStatus() == OrderStatus.CREATED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você só pode avaliar pedidos Pagos ou Entregues.");
        }

        // Verifica se o produto estava no pedido
        boolean productInOrder = order.getProducts().stream()
                .anyMatch(item -> item.getProductId().equals(product.getProductId()));

        if (!productInOrder) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Este produto não consta no pedido informado.");
        }

        // 5. Verifica duplicidade
        if (reviewRepository.existsByOrderIdAndProductProductId(request.getOrderId(), request.getProductId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Você já avaliou este produto nesta compra.");
        }

        // 6. Salva Review
        ReviewEntity review = ReviewEntity.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .user(user)
                .product(product)
                .orderId(request.getOrderId())
                .build();

        review = reviewRepository.save(review);

        // 7. Recalcula a média do produto
        updateProductRating(product);

        return mapToResponse(review);
    }

    @Override
    public List<DTOReviewResponse> getProductReviews(String productId) {
        return reviewRepository.findByProductProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private void updateProductRating(ProductEntity product) {
        List<ReviewEntity> reviews = reviewRepository.findByProductProductIdOrderByCreatedAtDesc(product.getProductId());

        if (reviews.isEmpty()) {
            product.setAverageRating(0.0);
            product.setReviewCount(0);
        } else {
            double average = reviews.stream()
                    .mapToInt(ReviewEntity::getRating)
                    .average()
                    .orElse(0.0);

            // Arredonda para 1 casa decimal
            average = Math.round(average * 10.0) / 10.0;

            product.setAverageRating(average);
            product.setReviewCount(reviews.size());
        }

        productRepository.save(product);
    }

    private DTOReviewResponse mapToResponse(ReviewEntity entity) {
        return DTOReviewResponse.builder()
                .id(entity.getId())
                .userName(entity.getUser().getName())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}