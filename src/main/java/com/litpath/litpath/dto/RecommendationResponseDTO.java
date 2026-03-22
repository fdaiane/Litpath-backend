package com.litpath.litpath.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecommendationResponseDTO {
    private List<BookResponseDTO> booksByGenre;
    private List<BookResponseDTO> booksByFollowing;
    private List<BookResponseDTO> popularBooks;
    private List<AuthorResponseDTO> authorsByGenre;
}