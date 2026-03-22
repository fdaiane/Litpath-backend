package com.litpath.litpath.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BookReviewRequestDTO {

    @NotNull(message = "Nota é obrigatória.")
    @Min(value = 1, message = "A nota mínima é 1.")
    @Max(value = 5, message = "A nota máxima é 5.")
    private Integer rating;
    private String comment;
}