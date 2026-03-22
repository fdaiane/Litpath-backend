package com.litpath.litpath.controller;

import com.litpath.litpath.dto.GenreDTO;
import com.litpath.litpath.service.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GenreDTO> createGenre(@RequestBody GenreDTO dto) {
        return ResponseEntity.ok(genreService.createGenre(dto));
    }

    @GetMapping
    public ResponseEntity<List<GenreDTO>> listGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}