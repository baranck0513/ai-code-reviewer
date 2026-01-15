package com.musti.codereviewer.service;

import com.musti.codereviewer.dto.CodeReviewRequest;
import com.musti.codereviewer.dto.CodeReviewResponse;
import com.musti.codereviewer.entity.Review;
import com.musti.codereviewer.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ClaudeService claudeService;

    @Transactional
    public CodeReviewResponse createReview(CodeReviewRequest request) {
        log.info("Creating new code review. Language: {}", request.getLanguage());

        // Get review from Claude AI
        String aiReview = claudeService.reviewCode(
                request.getCode(),
                request.getLanguage(),
                request.getDescription()
        );

        // Save to database
        Review review = Review.builder()
                .code(request.getCode())
                .language(request.getLanguage())
                .description(request.getDescription())
                .review(aiReview)
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Review saved with ID: {}", savedReview.getId());

        return mapToResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public List<CodeReviewResponse> getAllReviews() {
        log.info("Fetching all reviews");
        return reviewRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CodeReviewResponse getReviewById(Long id) {
        log.info("Fetching review with ID: {}", id);
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with ID: " + id));
        return mapToResponse(review);
    }

    @Transactional
    public void deleteReview(Long id) {
        log.info("Deleting review with ID: {}", id);
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found with ID: " + id);
        }
        reviewRepository.deleteById(id);
        log.info("Review deleted successfully");
    }

    private CodeReviewResponse mapToResponse(Review review) {
        return CodeReviewResponse.builder()
                .id(review.getId())
                .code(review.getCode())
                .language(review.getLanguage())
                .description(review.getDescription())
                .review(review.getReview())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
