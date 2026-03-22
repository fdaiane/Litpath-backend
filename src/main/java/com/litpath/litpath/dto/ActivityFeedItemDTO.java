package com.litpath.litpath.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ActivityFeedItemDTO {

    private String type;
    private LocalDateTime createdAt;

    // Para avaliações e listas
    private Long bookId;
    private String bookTitle;
    private Integer rating;
    private String comment;

    // Para favoritos de autor
    private Long authorId;
    private String authorName;

    // Para follows
    private Long followedUserId;
    private String followedUsername;

    // Para reações dadas
    private String reactionType; // LIKE | DISLIKE
    private String reviewBookTitle;

    // Para notificações recebidas (quem interagiu com minha avaliação)
    private Long fromUserId;
    private String fromUsername;
}