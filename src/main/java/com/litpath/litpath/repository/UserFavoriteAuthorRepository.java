package com.litpath.litpath.repository;
 
import com.litpath.litpath.model.UserFavoriteAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Optional;
 
@Repository
public interface UserFavoriteAuthorRepository extends JpaRepository<UserFavoriteAuthor, Long> {
    List<UserFavoriteAuthor> findByUserId(Long userId);
    Optional<UserFavoriteAuthor> findByUserIdAndAuthorId(Long userId, Long authorId);
    boolean existsByUserIdAndAuthorId(Long userId, Long authorId);
    void deleteByUserIdAndAuthorId(Long userId, Long authorId);
    void deleteByUserId(Long userId);   
}