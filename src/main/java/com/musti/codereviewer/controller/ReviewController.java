package com.musti.codereviewer.controller;

import com.musti.codereviewer.dto.CodeReviewRequest;
import com.musti.codereviewer.dto.CodeReviewResponse;
import com.musti.codereviewer.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * POST /api/reviews - Submit code for AI review
     */
    @PostMapping
    public ResponseEntity<CodeReviewResponse> createReview(
            @Valid @RequestBody CodeReviewRequest request) {
        log.info("Received code review request");
        CodeReviewResponse response = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/reviews - Get all reviews
     */
    @GetMapping
    public ResponseEntity<List<CodeReviewResponse>> getAllReviews() {
        log.info("Fetching all reviews");
        List<CodeReviewResponse> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    /**
     * GET /api/reviews/{id} - Get single review by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CodeReviewResponse> getReviewById(@PathVariable Long id) {
        log.info("Fetching review with ID: {}", id);
        CodeReviewResponse response = reviewService.getReviewById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/reviews/{id} - Delete a review
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        log.info("Deleting review with ID: {}", id);
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
