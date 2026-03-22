package com.litpath.litpath.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class UserPreferencesRequestDTO {

    @NotEmpty(message = "Selecione ao menos um gênero.")
    private Set<Long> genreIds;
}
