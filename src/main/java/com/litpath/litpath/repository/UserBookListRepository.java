package com.litpath.litpath.repository;

import com.litpath.litpath.model.ListType;
import com.litpath.litpath.model.UserBookList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookListRepository extends JpaRepository<UserBookList, Long> {

    List<UserBookList> findByUserId(Long userId);

    Optional<UserBookList> findByUserIdAndListType(Long userId, ListType listType);

    void deleteByUserId(Long userId);
}