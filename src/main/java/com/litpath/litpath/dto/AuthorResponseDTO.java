package com.litpath.litpath.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class AuthorResponseDTO {
    private Long id;
    private String name;
    private String biography;
    private String nationality;
    private LocalDate birthDate;
    private String photoUrl;
    private Set<GenreDTO> genres;
    private List<BookResponseDTO> books;
}