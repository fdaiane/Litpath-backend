package com.litpath.litpath.controller;

import com.litpath.litpath.dto.AuthorRequestDTO;
import com.litpath.litpath.dto.AuthorResponseDTO;
import com.litpath.litpath.model.Author;
import com.litpath.litpath.service.AuthorService;
import com.litpath.litpath.service.OpenLibraryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/authors")
public class AuthorController {

    private final AuthorService authorService;
    private final OpenLibraryService openLibraryService;

    public AuthorController(AuthorService authorService,
                            OpenLibraryService openLibraryService) {
        this.authorService = authorService;
        this.openLibraryService = openLibraryService;
    }

    @PostMapping("/import")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDTO> importFromOpenLibrary(
            @RequestParam String name) {

        Author author = openLibraryService.importAuthorByName(name);
        return ResponseEntity.ok(authorService.toDTO(author));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDTO> createAuthor(
            @Valid @RequestBody AuthorRequestDTO dto) {
        return ResponseEntity.ok(authorService.createAuthorFromOpenLibrary(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorResponseDTO> updateAuthor(
            @PathVariable Long id,
            @RequestBody AuthorRequestDTO dto) {
        return ResponseEntity.ok(authorService.updateAuthor(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<AuthorResponseDTO>> listAuthors() {
        return ResponseEntity.ok(authorService.getAllAuthors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorResponseDTO> getAuthor(@PathVariable Long id) {
        return ResponseEntity.ok(authorService.getAuthorById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<AuthorResponseDTO>> searchAuthors(
            @RequestParam String name) {
        return ResponseEntity.ok(authorService.searchByName(name));
    }
}