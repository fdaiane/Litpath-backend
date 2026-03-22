package com.litpath.litpath.repository;

import com.litpath.litpath.model.ReactionType;
import com.litpath.litpath.model.ReviewCommentReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewCommentReactionRepository extends JpaRepository<ReviewCommentReaction, Long> {
    Optional<ReviewCommentReaction> findByUserIdAndCommentId(Long userId, Long commentId);
    boolean existsByUserIdAndCommentId(Long userId, Long commentId);
    int countByCommentIdAndReactionType(Long commentId, ReactionType reactionType);
    void deleteByUserId(Long userId);          
    void deleteByCommentId(Long commentId);
}