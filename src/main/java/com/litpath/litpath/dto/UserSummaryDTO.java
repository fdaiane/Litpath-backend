package com.litpath.litpath.dto;

import lombok.Data;

@Data
public class UserSummaryDTO {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String photoUrl;
    private boolean following; 
}