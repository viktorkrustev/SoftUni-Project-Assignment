package com.onlineshop.service;

import com.onlineshop.model.dto.ReviewViewDTO;
import com.onlineshop.model.entity.Review;
import com.onlineshop.repository.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
    }

    public List<ReviewViewDTO> getReviewsForProduct(Long productId) {
        List<Review> reviews = reviewRepository.findByProduct_Id(productId);
        return reviews.stream()
                .map(review -> modelMapper.map(review, ReviewViewDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public void saveReview(Review review) {
        reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        reviewRepository.deleteById(reviewId);
    }
}

