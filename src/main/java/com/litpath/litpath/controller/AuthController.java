package com.litpath.litpath.controller;

import com.litpath.litpath.dto.LoginRequestDTO;
import com.litpath.litpath.dto.LoginResponseDTO;
import com.litpath.litpath.dto.UserResponseDTO;
import com.litpath.litpath.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request) {

        String token = userService.login(request);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> me(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }
}