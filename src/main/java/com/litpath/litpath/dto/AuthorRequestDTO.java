package com.litpath.litpath.dto;

import java.time.LocalDate;
import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthorRequestDTO {

    @NotBlank(message = "Nome do autor é obrigatório")
    private String name;
    private String biography;
    private String nationality;
    private LocalDate birthDate;
    private String photoUrl;
    private Set<Long> genreIds;
}