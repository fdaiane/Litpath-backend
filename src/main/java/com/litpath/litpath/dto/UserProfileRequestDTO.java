package com.litpath.litpath.dto;

import lombok.Data;

@Data
public class UserProfileRequestDTO {
    private String firstName;   
    private String lastName;    
    private String photoUrl;
    private String bio;
    private String city;
    private String birthDate;
}