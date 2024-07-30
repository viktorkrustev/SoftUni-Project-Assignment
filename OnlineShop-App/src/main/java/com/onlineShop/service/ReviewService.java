package com.onlineshop.service;

import com.onlineshop.model.dto.ReviewDTO;
import com.onlineshop.model.dto.ReviewViewDTO;

import java.util.List;

public interface ReviewService {
    List<ReviewViewDTO> getReviewsForProduct(Long productId);

    void deleteReview(Long reviewId);

    void addReview(Long productId, ReviewDTO reviewDTO);
}
