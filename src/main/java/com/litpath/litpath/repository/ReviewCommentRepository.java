package com.litpath.litpath.repository;
 
import com.litpath.litpath.model.ReviewComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
 
@Repository
public interface ReviewCommentRepository extends JpaRepository<ReviewComment, Long> {
    List<ReviewComment> findByReviewId(Long reviewId);
    List<ReviewComment> findByUserId(Long userId);
    void deleteByUserId(Long userId);       // ← novo
    void deleteByReviewId(Long reviewId);   // ← novo
}