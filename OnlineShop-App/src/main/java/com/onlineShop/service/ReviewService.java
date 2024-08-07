package com.onlineShop.service;

import com.onlineShop.model.dto.ReviewDTO;
import com.onlineShop.model.dto.ReviewViewDTO;

import java.util.List;

public interface ReviewService {
    List<ReviewViewDTO> getReviewsForProduct(Long productId);

    void deleteReview(Long reviewId);

    void addReview(Long productId, ReviewDTO reviewDTO);
}
