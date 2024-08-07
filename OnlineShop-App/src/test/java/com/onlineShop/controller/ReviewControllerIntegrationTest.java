package com.onlineShop.controller;

import com.onlineShop.model.dto.ReviewDTO;
import com.onlineShop.model.entity.Product;
import com.onlineShop.service.ProductService;
import com.onlineShop.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReviewController.class)
public class ReviewControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        Mockito.when(productService.getProductById(anyLong())).thenReturn(new Product());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAddReviewWithValidInput() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/products/1/add-review")
                        .param("rating", "5")
                        .param("comment", "Great product!")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/products/1"));

        Mockito.verify(reviewService).addReview(anyLong(), any(ReviewDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAddReviewWithValidationErrors() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/products/1/add-review")
                        .param("rating", "")
                        .param("comment", "")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/products/1"));

        Mockito.verify(reviewService, Mockito.never()).addReview(anyLong(), any(ReviewDTO.class));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testDeleteReview() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/reviews/1/delete")
                        .param("productId", "1")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/products/1"));

        Mockito.verify(reviewService).deleteReview(anyLong());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testAddReviewWhenReviewServiceThrowsException() throws Exception {
        Mockito.doThrow(new IllegalArgumentException("Product not found"))
                .when(reviewService).addReview(anyLong(), any(ReviewDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/products/1/add-review")
                        .param("rating", "5")
                        .param("comment", "Great product!")
                        .with(csrf()))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/products"));

        Mockito.verify(reviewService).addReview(anyLong(), any(ReviewDTO.class));
    }
}
