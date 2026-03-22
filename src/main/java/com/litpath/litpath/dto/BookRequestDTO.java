package com.litpath.litpath.dto;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookRequestDTO {

    @NotBlank(message = "Título é obrigatório")
    private String title;
    private String synopsis;
    private Integer publicationYear;
    private String coverUrl;

    @NotNull(message = "Autor é obrigatório")
    private Long authorId;
    private Set<Long> genreIds;
}
