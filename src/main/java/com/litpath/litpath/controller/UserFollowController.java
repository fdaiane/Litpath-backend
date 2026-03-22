package com.litpath.litpath.controller;

import com.litpath.litpath.dto.UserFollowResponseDTO;
import com.litpath.litpath.dto.UserSummaryDTO;
import com.litpath.litpath.service.UserFollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/social")
public class UserFollowController {

    private final UserFollowService userFollowService;

    public UserFollowController(UserFollowService userFollowService) {
        this.userFollowService = userFollowService;
    }

   
    @PostMapping("/follow/{userId}")
    public ResponseEntity<Void> follow(
            Authentication authentication,
            @PathVariable Long userId) {
        userFollowService.follow(authentication.getName(), userId);
        return ResponseEntity.noContent().build();
    }

   
    @DeleteMapping("/follow/{userId}")
    public ResponseEntity<Void> unfollow(
            Authentication authentication,
            @PathVariable Long userId) {
        userFollowService.unfollow(authentication.getName(), userId);
        return ResponseEntity.noContent().build();
    }

    
    @GetMapping("/me")
    public ResponseEntity<UserFollowResponseDTO> getMyFollowData(Authentication authentication) {
        return ResponseEntity.ok(userFollowService.getMyFollowData(authentication.getName()));
    }

    
    @GetMapping("/users/{userId}")
    public ResponseEntity<UserFollowResponseDTO> getFollowDataByUser(
            Authentication authentication,
            @PathVariable Long userId) {
        return ResponseEntity.ok(userFollowService.getFollowDataByUser(userId, authentication.getName()));
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserSummaryDTO>> searchUsers(
            @RequestParam String name,
            Authentication authentication) {
        return ResponseEntity.ok(userFollowService.searchUsers(name, authentication.getName()));
    }
}