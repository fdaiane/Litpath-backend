package com.litpath.litpath.repository;

import com.litpath.litpath.model.UserGenrePreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGenrePreferenceRepository extends JpaRepository<UserGenrePreference, Long> {
    List<UserGenrePreference> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}