package com.litpath.litpath.controller;

import com.litpath.litpath.dto.UserBookListResponseDTO;
import com.litpath.litpath.model.ListType;
import com.litpath.litpath.service.BookListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lists")
public class BookListController {

    private final BookListService bookListService;

    public BookListController(BookListService bookListService) {
        this.bookListService = bookListService;
    }

    @GetMapping
    public ResponseEntity<List<UserBookListResponseDTO>> getAllLists(Authentication authentication) {
        return ResponseEntity.ok(bookListService.getAllLists(authentication.getName()));
    }

    @GetMapping("/{listType}")
    public ResponseEntity<UserBookListResponseDTO> getList(
            Authentication authentication,
            @PathVariable ListType listType) {
        return ResponseEntity.ok(bookListService.getList(authentication.getName(), listType));
    }

    @PostMapping("/{listType}/books/{bookId}")
    public ResponseEntity<UserBookListResponseDTO> addBookToList(
            Authentication authentication,
            @PathVariable ListType listType,
            @PathVariable Long bookId) {
        return ResponseEntity.ok(bookListService.addBookToList(authentication.getName(), listType, bookId));
    }

    @DeleteMapping("/{listType}/books/{bookId}")
    public ResponseEntity<Void> removeBookFromList(
            Authentication authentication,
            @PathVariable ListType listType,
            @PathVariable Long bookId) {
        bookListService.removeBookFromList(authentication.getName(), listType, bookId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/{listType}")
    public ResponseEntity<UserBookListResponseDTO> getListByUser(
            @PathVariable Long userId,
            @PathVariable ListType listType) {
        return ResponseEntity.ok(bookListService.getListByUserId(userId, listType));
    }
}