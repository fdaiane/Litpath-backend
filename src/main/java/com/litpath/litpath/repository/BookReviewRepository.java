package com.litpath.litpath.repository;
 
import com.litpath.litpath.model.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Optional;
 
@Repository
public interface BookReviewRepository extends JpaRepository<BookReview, Long> {
    List<BookReview> findByBookId(Long bookId);
    List<BookReview> findByUserId(Long userId);
    Optional<BookReview> findByUserIdAndBookId(Long userId, Long bookId);
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
    void deleteByUserId(Long userId);   
 
    @Query("SELECT AVG(r.rating) FROM BookReview r WHERE r.book.id = :bookId")
    Double findAverageRatingByBookId(Long bookId);
}