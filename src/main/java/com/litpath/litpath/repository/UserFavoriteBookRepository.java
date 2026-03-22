package com.litpath.litpath.repository;
 
import com.litpath.litpath.model.UserFavoriteBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Optional;
 
@Repository
public interface UserFavoriteBookRepository extends JpaRepository<UserFavoriteBook, Long> {
    List<UserFavoriteBook> findByUserId(Long userId);
    Optional<UserFavoriteBook> findByUserIdAndBookId(Long userId, Long bookId);
    boolean existsByUserIdAndBookId(Long userId, Long bookId);
    void deleteByUserIdAndBookId(Long userId, Long bookId);
    void deleteByUserId(Long userId);   
}