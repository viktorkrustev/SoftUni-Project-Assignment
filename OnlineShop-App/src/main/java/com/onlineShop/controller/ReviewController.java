package com.onlineshop.controller;

import com.onlineshop.model.dto.ReviewDTO;
import com.onlineshop.model.dto.ReviewViewDTO;
import com.onlineshop.model.entity.Product;
import com.onlineshop.model.entity.Review;
import com.onlineshop.model.entity.User;
import com.onlineshop.service.ProductService;
import com.onlineshop.service.ReviewService;
import com.onlineshop.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReviewController {
    private final UserService userService;
    private final ProductService productService;
    private final ReviewService reviewService;
    private final ModelMapper modelMapper;

    public ReviewController(UserService userService, ProductService productService, ReviewService reviewService, ModelMapper modelMapper) {
        this.userService = userService;
        this.productService = productService;
        this.reviewService = reviewService;
        this.modelMapper = modelMapper;
    }


    @PostMapping("/products/{productId}/add-review")
    public String addReview(@PathVariable Long productId, @ModelAttribute("reviewDTO") ReviewDTO reviewDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User user = userService.findByUsername(username);
        Product product = productService.getProductById(productId);

        if (product == null) {
            return "redirect:/products";
        }

        Review review = modelMapper.map(reviewDTO, Review.class);
        review.setUser(user);
        review.setProduct(product);

        reviewService.saveReview(review);

        return "redirect:/products/" + productId;
    }

    @PostMapping("/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId, @RequestParam Long productId) {
        reviewService.deleteReview(reviewId);
        return "redirect:/products/" + productId;
    }
}
