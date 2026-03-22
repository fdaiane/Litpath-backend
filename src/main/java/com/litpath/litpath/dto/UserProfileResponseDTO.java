package com.litpath.litpath.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserProfileResponseDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String photoUrl;
    private String bio;
    private String city;
    private String birthDate;
    private boolean firstLogin;
    private List<GenreDTO> preferences;
}
