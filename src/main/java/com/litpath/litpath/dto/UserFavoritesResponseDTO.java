package com.litpath.litpath.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserFavoritesResponseDTO {
    private List<BookResponseDTO> favoriteBooks;
    private List<AuthorResponseDTO> favoriteAuthors;
}