package com.litpath.litpath.controller;

import com.litpath.litpath.dto.UserPreferencesRequestDTO;
import com.litpath.litpath.dto.UserProfileRequestDTO;
import com.litpath.litpath.dto.UserProfileResponseDTO;
import com.litpath.litpath.service.UserProfileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    // BUSCAR MEU PERFIL
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> getMyProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(userProfileService.getMyProfile(email));
    }

    // BUSCAR PERFIL PÚBLICO DE OUTRO USUÁRIO
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponseDTO> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getProfileById(userId));
    }

    // EDITAR MEU PERFIL
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> updateProfile(
            Authentication authentication,
            @RequestBody UserProfileRequestDTO dto) {
        String email = authentication.getName();
        return ResponseEntity.ok(userProfileService.updateProfile(email, dto));
    }

    // SALVAR PREFERÊNCIAS (primeiro login ou reedição)
    @PostMapping("/preferences")
    public ResponseEntity<UserProfileResponseDTO> savePreferences(
            Authentication authentication,
            @Valid @RequestBody UserPreferencesRequestDTO dto) {
        String email = authentication.getName();
        return ResponseEntity.ok(userProfileService.savePreferences(email, dto));
    }
}