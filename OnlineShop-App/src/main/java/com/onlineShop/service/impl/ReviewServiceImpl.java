package com.onlineShop.service.impl;

import com.onlineShop.model.dto.ReviewDTO;
import com.onlineShop.model.dto.ReviewViewDTO;
import com.onlineShop.model.entity.Product;
import com.onlineShop.model.entity.Review;
import com.onlineShop.model.entity.User;
import com.onlineShop.repository.ReviewRepository;
import com.onlineShop.service.ReviewService;
import com.onlineShop.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {
    private final UserServiceImpl userService;
    private final ProductService productService;
    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ReviewServiceImpl(UserServiceImpl userService, ProductService productService, ReviewRepository reviewRepository, ModelMapper modelMapper) {
        this.userService = userService;
        this.productService = productService;
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<ReviewViewDTO> getReviewsForProduct(Long productId) {
        List<Review> reviews = reviewRepository.findByProduct_Id(productId);
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewViewDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    @Override
    @Transactional
    public void addReview(Long productId, ReviewDTO reviewDTO) {
        User user = userService.getCurrentUser();
        Product product = productService.getProductById(productId);

        if (product == null) {
            throw new IllegalArgumentException("Product not found");
        }

        Review review = modelMapper.map(reviewDTO, Review.class);
        review.setUser(user);
        review.setProduct(product);

        reviewRepository.save(review);
    }
}
