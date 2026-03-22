package com.litpath.litpath.repository;

import com.litpath.litpath.model.ReactionType;
import com.litpath.litpath.model.ReviewReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewReactionRepository extends JpaRepository<ReviewReaction, Long> {

    // Usado pelo BookReviewService
    Optional<ReviewReaction> findByUserIdAndReviewId(Long userId, Long reviewId);

    // Usado pelo ActivityFeedService — notificações recebidas
    List<ReviewReaction> findByReviewId(Long reviewId);

    // Usado pelo ActivityFeedService — o que eu fiz
    List<ReviewReaction> findByUserId(Long userId);

    // Contagem para toReviewDTO()
    int countByReviewIdAndReactionType(Long reviewId, ReactionType reactionType);

    // Cascade delete
    void deleteByReviewId(Long reviewId);
    void deleteByUserId(Long userId);
}