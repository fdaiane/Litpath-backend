package com.litpath.litpath.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewCommentRequestDTO {

    @NotBlank(message = "Comentário é obrigatório.")
    private String content;
}