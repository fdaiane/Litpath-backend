package com.litpath.litpath.repository;

import com.litpath.litpath.model.UserBookListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBookListItemRepository extends JpaRepository<UserBookListItem, Long> {
    Optional<UserBookListItem> findByUserBookListIdAndBookId(Long listId, Long bookId);
    boolean existsByUserBookListIdAndBookId(Long listId, Long bookId);
    void deleteByUserBookListIdAndBookId(Long listId, Long bookId);
}