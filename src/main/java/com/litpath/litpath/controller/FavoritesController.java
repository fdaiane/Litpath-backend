package com.litpath.litpath.controller;

import com.litpath.litpath.dto.UserFavoritesResponseDTO;
import com.litpath.litpath.service.FavoritesService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favorites")
public class FavoritesController {

    private final FavoritesService favoritesService;

    public FavoritesController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @GetMapping
    public ResponseEntity<UserFavoritesResponseDTO> getMyFavorites(Authentication authentication) {
        return ResponseEntity.ok(favoritesService.getMyFavorites(authentication.getName()));
    }

    @PostMapping("/books/{bookId}")
    public ResponseEntity<Void> addFavoriteBook(
            Authentication authentication,
            @PathVariable Long bookId) {
        favoritesService.addFavoriteBook(authentication.getName(), bookId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<Void> removeFavoriteBook(
            Authentication authentication,
            @PathVariable Long bookId) {
        favoritesService.removeFavoriteBook(authentication.getName(), bookId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/authors/{authorId}")
    public ResponseEntity<Void> addFavoriteAuthor(
            Authentication authentication,
            @PathVariable Long authorId) {
        favoritesService.addFavoriteAuthor(authentication.getName(), authorId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/authors/{authorId}")
    public ResponseEntity<Void> removeFavoriteAuthor(
            Authentication authentication,
            @PathVariable Long authorId) {
        favoritesService.removeFavoriteAuthor(authentication.getName(), authorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserFavoritesResponseDTO> getFavoritesByUser(
            @PathVariable Long userId) {
        return ResponseEntity.ok(favoritesService.getFavoritesByUserId(userId));
    }
}