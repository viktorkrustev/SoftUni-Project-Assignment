package com.onlineshop.service;

import com.onlineshop.model.dto.ReviewDTO;
import com.onlineshop.model.dto.ReviewViewDTO;
import com.onlineshop.model.entity.Product;
import com.onlineshop.model.entity.Review;
import com.onlineshop.model.entity.User;
import com.onlineshop.repository.ReviewRepository;
import com.onlineshop.service.ProductService;
import com.onlineshop.service.UserService;
import com.onlineshop.service.impl.ReviewServiceImpl;
import com.onlineshop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplTest {

    @Mock
    private UserServiceImpl userService;

    @Mock
    private ProductService productService;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Product product;
    private User user;
    private Review review;
    private ReviewDTO reviewDTO;
    private ReviewViewDTO reviewViewDTO;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Product 1");

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        review = new Review();
        review.setId(1L);
        review.setProduct(product);
        review.setUser(user);
        review.setRating(5);
        review.setComment("Excellent product!");

        reviewDTO = new ReviewDTO();
        reviewDTO.setRating(5);
        reviewDTO.setComment("Excellent product!");

        reviewViewDTO = new ReviewViewDTO();
        reviewViewDTO.setRating(5);
        reviewViewDTO.setComment("Excellent product!");
    }

    @Test
    void testGetReviewsForProduct() {
        when(reviewRepository.findByProduct_Id(1L)).thenReturn(Arrays.asList(review));
        when(modelMapper.map(review, ReviewViewDTO.class)).thenReturn(reviewViewDTO);

        List<ReviewViewDTO> result = reviewService.getReviewsForProduct(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(5, result.get(0).getRating());
        assertEquals("Excellent product!", result.get(0).getComment());
        verify(reviewRepository, times(1)).findByProduct_Id(1L);
    }

    @Test
    void testDeleteReview() {
        reviewService.deleteReview(1L);
        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void testAddReview_ProductNotFound() {
        when(productService.getProductById(1L)).thenReturn(null);

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> reviewService.addReview(1L, reviewDTO)
        );

        assertEquals("Product not found", thrown.getMessage());
    }

    @Test
    void testAddReview_Success() {
        when(productService.getProductById(1L)).thenReturn(product);
        when(userService.getCurrentUser()).thenReturn(user);
        when(modelMapper.map(reviewDTO, Review.class)).thenReturn(review);
        when(reviewRepository.save(review)).thenReturn(review);

        reviewService.addReview(1L, reviewDTO);

        verify(reviewRepository, times(1)).save(review);
        assertEquals(product, review.getProduct());
        assertEquals(user, review.getUser());
    }
}
