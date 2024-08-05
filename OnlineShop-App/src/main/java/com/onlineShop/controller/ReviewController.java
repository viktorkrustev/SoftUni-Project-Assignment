package com.onlineshop.controller;

import com.onlineshop.model.dto.ReviewDTO;
import com.onlineshop.service.ProductService;
import com.onlineshop.service.ReviewService;
import com.onlineshop.service.impl.ReviewServiceImpl;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReviewController {
    private final ProductService productService;
    private final ReviewService reviewService;

    public ReviewController(ProductService productService, ReviewService reviewService) {
        this.productService = productService;
        this.reviewService = reviewService;
    }


    @PostMapping("/products/{productId}/add-review")
    public String addReview(@PathVariable Long productId, @ModelAttribute("reviewDTO") @Valid ReviewDTO reviewDTO, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("reviewDTO", reviewDTO);
            model.addAttribute("product", productService.getProductById(productId));
            return "redirect:/products/" + productId;
        }

        try {
            reviewService.addReview(productId, reviewDTO);
        } catch (IllegalArgumentException e) {
            return "redirect:/products";
        }

        return "redirect:/products/" + productId;
    }



    @PostMapping("/reviews/{reviewId}/delete")
    public String deleteReview(@PathVariable Long reviewId, @RequestParam Long productId) {
        reviewService.deleteReview(reviewId);
        return "redirect:/products/" + productId;
    }
}
