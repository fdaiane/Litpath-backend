package com.litpath.litpath.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequestDTO {

    @NotBlank(message = "Nome é obrigatório.")
    private String firstName;

    @NotBlank(message = "Sobrenome é obrigatório.")
    private String lastName;

    @NotBlank(message = "Email é obrigatório.")
    @Pattern(
        regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message = "Informe um email válido (ex: usuario@email.com)"
    )
    private String email;

    @NotBlank(message = "Nome de usuário é obrigatório.")
    private String username;

    @NotBlank(message = "Senha é obrigatória.")
    @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
    message = "A senha deve ter no mínimo 8 caracteres, incluindo letra maiúscula, minúscula, número e caractere especial."
    )
    private String password;

    @NotBlank(message = "Confirmação de senha é obrigatória.")
    private String confirmPassword;
}
 