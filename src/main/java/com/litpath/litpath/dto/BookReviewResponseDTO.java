package com.litpath.litpath.dto;

import com.litpath.litpath.model.ReactionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookReviewResponseDTO {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private Long userId;
    private String username;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likes;
    private int dislikes;
    private ReactionType myReaction;
    private List<ReviewCommentResponseDTO> comments;
}