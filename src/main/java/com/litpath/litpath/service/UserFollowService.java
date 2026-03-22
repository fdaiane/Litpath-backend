package com.litpath.litpath.service;

import com.litpath.litpath.dto.UserFollowResponseDTO;
import com.litpath.litpath.dto.UserSummaryDTO;
import com.litpath.litpath.exception.BusinessException;
import com.litpath.litpath.exception.ResourceNotFoundException;
import com.litpath.litpath.model.User;
import com.litpath.litpath.model.UserFollow;
import com.litpath.litpath.repository.UserFollowRepository;
import com.litpath.litpath.repository.UserProfileRepository;
import com.litpath.litpath.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserFollowService {

    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;
    private final UserProfileRepository userProfileRepository;

    public UserFollowService(UserRepository userRepository,
                              UserFollowRepository userFollowRepository,
                              UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userFollowRepository = userFollowRepository;
        this.userProfileRepository = userProfileRepository;
    }

   
    @Transactional
    public void follow(String email, Long targetUserId) {
        User follower = findUserByEmail(email);
        User following = findUserById(targetUserId);

        if (follower.getId().equals(targetUserId)) {
            throw new BusinessException("Você não pode seguir a si mesmo!");
        }

        if (userFollowRepository.existsByFollowerIdAndFollowingId(follower.getId(), targetUserId)) {
            throw new BusinessException("Você já segue este usuário!");
        }

        UserFollow userFollow = new UserFollow();
        userFollow.setFollower(follower);
        userFollow.setFollowing(following);
        userFollow.setCreatedAt(LocalDateTime.now());

        userFollowRepository.save(userFollow);
    }


    @Transactional
    public void unfollow(String email, Long targetUserId) {
        User follower = findUserByEmail(email);

        UserFollow userFollow = userFollowRepository
                .findByFollowerIdAndFollowingId(follower.getId(), targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Você não segue este usuário!"));

        userFollowRepository.delete(userFollow);
    }


    public UserFollowResponseDTO getMyFollowData(String email) {
        User user = findUserByEmail(email);
        return toFollowDTO(user, user.getId());
    }

    
    public UserFollowResponseDTO getFollowDataByUser(Long userId, String email) {
        User loggedUser = findUserByEmail(email);
        User targetUser = findUserById(userId);
        return toFollowDTO(targetUser, loggedUser.getId());
    }

 
    public List<UserSummaryDTO> searchUsers(String name, String email) {
        User loggedUser = findUserByEmail(email);

        return userRepository.findByFirstNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(name, name)
                .stream()
                .filter(u -> !u.getId().equals(loggedUser.getId()))
                .map(u -> toSummaryDTO(u, loggedUser.getId()))
                .collect(Collectors.toList());
    }

 
    private UserFollowResponseDTO toFollowDTO(User user, Long loggedUserId) {
        UserFollowResponseDTO dto = new UserFollowResponseDTO();

        List<UserSummaryDTO> followers = userFollowRepository.findByFollowingId(user.getId())
                .stream()
                .map(f -> toSummaryDTO(f.getFollower(), loggedUserId))
                .collect(Collectors.toList());

        List<UserSummaryDTO> following = userFollowRepository.findByFollowerId(user.getId())
                .stream()
                .map(f -> toSummaryDTO(f.getFollowing(), loggedUserId))
                .collect(Collectors.toList());

        dto.setFollowers(followers);
        dto.setFollowing(following);
        dto.setFollowersCount(followers.size());
        dto.setFollowingCount(following.size());

        return dto;
    }

 
    private UserSummaryDTO toSummaryDTO(User user, Long loggedUserId) {
        UserSummaryDTO dto = new UserSummaryDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setFollowing(userFollowRepository.existsByFollowerIdAndFollowingId(loggedUserId, user.getId()));

        
        userProfileRepository.findByUserId(user.getId())
                .ifPresent(profile -> dto.setPhotoUrl(profile.getPhotoUrl()));

        return dto;
    }

 
    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado!"));
    }
}