package com.litpath.litpath.dto;

import com.litpath.litpath.model.ReactionType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewCommentResponseDTO {
    private Long id;
    private Long reviewId;
    private Long userId;
    private String username;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int likes;
    private int dislikes;
    private ReactionType myReaction;
}