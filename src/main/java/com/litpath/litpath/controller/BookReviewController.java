package com.litpath.litpath.controller;

import com.litpath.litpath.dto.*;
import com.litpath.litpath.service.BookReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class BookReviewController {

    private final BookReviewService bookReviewService;

    public BookReviewController(BookReviewService bookReviewService) {
        this.bookReviewService = bookReviewService;
    }

    @PostMapping("/books/{bookId}")
    public ResponseEntity<BookReviewResponseDTO> createReview(
            Authentication authentication,
            @PathVariable Long bookId,
            @Valid @RequestBody BookReviewRequestDTO dto) {
        return ResponseEntity.ok(bookReviewService.createReview(authentication.getName(), bookId, dto));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<BookReviewResponseDTO> updateReview(
            Authentication authentication,
            @PathVariable Long reviewId,
            @Valid @RequestBody BookReviewRequestDTO dto) {
        return ResponseEntity.ok(bookReviewService.updateReview(authentication.getName(), reviewId, dto));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            Authentication authentication,
            @PathVariable Long reviewId) {
        bookReviewService.deleteReview(authentication.getName(), reviewId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<List<BookReviewResponseDTO>> getReviewsByBook(
            Authentication authentication,
            @PathVariable Long bookId) {
        return ResponseEntity.ok(bookReviewService.getReviewsByBook(bookId, authentication.getName()));
    }

    @GetMapping("/me")
    public ResponseEntity<List<BookReviewResponseDTO>> getMyReviews(Authentication authentication) {
        return ResponseEntity.ok(bookReviewService.getMyReviews(authentication.getName()));
    }

    @GetMapping("/books/{bookId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long bookId) {
        return ResponseEntity.ok(bookReviewService.getAverageRating(bookId));
    }


    @PostMapping("/{reviewId}/reactions")
    public ResponseEntity<BookReviewResponseDTO> reactToReview(
            Authentication authentication,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewReactionRequestDTO dto) {
        return ResponseEntity.ok(bookReviewService.reactToReview(authentication.getName(), reviewId, dto));
    }

    @DeleteMapping("/{reviewId}/reactions")
    public ResponseEntity<Void> removeReactionFromReview(
            Authentication authentication,
            @PathVariable Long reviewId) {
        bookReviewService.removeReactionFromReview(authentication.getName(), reviewId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{reviewId}/comments")
    public ResponseEntity<ReviewCommentResponseDTO> addComment(
            Authentication authentication,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewCommentRequestDTO dto) {
        return ResponseEntity.ok(bookReviewService.addComment(authentication.getName(), reviewId, dto));
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<ReviewCommentResponseDTO> updateComment(
            Authentication authentication,
            @PathVariable Long commentId,
            @Valid @RequestBody ReviewCommentRequestDTO dto) {
        return ResponseEntity.ok(bookReviewService.updateComment(authentication.getName(), commentId, dto));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            Authentication authentication,
            @PathVariable Long commentId) {
        bookReviewService.deleteComment(authentication.getName(), commentId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/comments/{commentId}/reactions")
    public ResponseEntity<ReviewCommentResponseDTO> reactToComment(
            Authentication authentication,
            @PathVariable Long commentId,
            @Valid @RequestBody ReviewReactionRequestDTO dto) {
        return ResponseEntity.ok(bookReviewService.reactToComment(authentication.getName(), commentId, dto));
    }

    @DeleteMapping("/comments/{commentId}/reactions")
    public ResponseEntity<Void> removeReactionFromComment(
            Authentication authentication,
            @PathVariable Long commentId) {
        bookReviewService.removeReactionFromComment(authentication.getName(), commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<BookReviewResponseDTO>> getReviewsByUser(
            @PathVariable Long userId,
            Authentication authentication) {
        return ResponseEntity.ok(bookReviewService.getReviewsByUserId(userId, authentication.getName()));
    }
}