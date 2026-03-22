package com.litpath.litpath.controller;

import com.litpath.litpath.dto.ActivityFeedItemDTO;
import com.litpath.litpath.service.ActivityFeedService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
public class ActivityFeedController {

    private final ActivityFeedService activityFeedService;

    public ActivityFeedController(ActivityFeedService activityFeedService) {
        this.activityFeedService = activityFeedService;
    }

    @GetMapping("/me")
    public ResponseEntity<List<ActivityFeedItemDTO>> getMyFeed(Authentication authentication) {
        return ResponseEntity.ok(activityFeedService.getMyFeed(authentication.getName()));
    }

    @GetMapping("/received")
    public ResponseEntity<List<ActivityFeedItemDTO>> getReceivedFeed(Authentication authentication) {
        return ResponseEntity.ok(activityFeedService.getReceivedFeed(authentication.getName()));
    }
}