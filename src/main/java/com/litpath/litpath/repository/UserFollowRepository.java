package com.litpath.litpath.repository;
 
import com.litpath.litpath.model.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import java.util.List;
import java.util.Optional;
 
@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {
    List<UserFollow> findByFollowerId(Long followerId);
    List<UserFollow> findByFollowingId(Long followingId);
    Optional<UserFollow> findByFollowerIdAndFollowingId(Long followerId, Long followingId);
    boolean existsByFollowerIdAndFollowingId(Long followerId, Long followingId);
    int countByFollowerId(Long followerId);
    int countByFollowingId(Long followingId);
    void deleteByFollowerId(Long followerId);   
    void deleteByFollowingId(Long followingId); 
}