package com.litpath.litpath.dto;

import com.litpath.litpath.model.ReactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewReactionRequestDTO {

    @NotNull(message = "Tipo de reação é obrigatório.")
    private ReactionType reactionType;
}