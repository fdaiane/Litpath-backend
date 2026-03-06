package com.litpath.litpath.dto;

import java.util.List;
import lombok.Data;

@Data
public class BookResponseDTO {
    private Long id;
    private String title;
    private String synopsis;
    private Integer publicationYear;
    private String coverUrl;
    private String authorName;
    private List<GenreDTO> genres;
}
