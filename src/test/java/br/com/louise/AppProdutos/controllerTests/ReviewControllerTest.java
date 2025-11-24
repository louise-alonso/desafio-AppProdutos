package br.com.louise.AppProdutos.controllerTests;

import br.com.louise.AppProdutos.controller.ReviewController;
import br.com.louise.AppProdutos.dto.review.DTOReviewRequest;
import br.com.louise.AppProdutos.dto.review.DTOReviewResponse;
import br.com.louise.AppProdutos.service.ReviewService;
import br.com.louise.AppProdutos.security.TokenService;
import br.com.louise.AppProdutos.security.AppUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private ReviewService reviewService;
    @MockBean private TokenService tokenService;
    @MockBean private AppUserDetailsService appUserDetailsService;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createReview_ShouldReturn201() throws Exception {
        DTOReviewRequest request = new DTOReviewRequest();
        request.setProductId("prod-1");
        request.setOrderId("order-123");
        request.setRating(5);
        request.setComment("Top!");

        DTOReviewResponse response = DTOReviewResponse.builder()
                .id(1L)
                .rating(5)
                .comment("Top!")
                .build();

        when(reviewService.createReview(any(DTOReviewRequest.class))).thenReturn(response);

        mockMvc.perform(post("/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void getProductReviews_ShouldReturnList() throws Exception {
        DTOReviewResponse review1 = DTOReviewResponse.builder().userName("Ana").rating(5).build();
        when(reviewService.getProductReviews(anyString())).thenReturn(List.of(review1));

        mockMvc.perform(get("/reviews/product/prod-1"))
                .andExpect(status().isOk());
    }
}