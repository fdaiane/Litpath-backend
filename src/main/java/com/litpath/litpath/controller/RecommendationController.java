package com.litpath.litpath.controller;

import com.litpath.litpath.dto.RecommendationResponseDTO;
import com.litpath.litpath.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }


    @GetMapping
    public ResponseEntity<RecommendationResponseDTO> getRecommendations(Authentication authentication) {
        return ResponseEntity.ok(recommendationService.getRecommendations(authentication.getName()));
    }
}