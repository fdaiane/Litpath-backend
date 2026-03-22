package com.litpath.litpath.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserFollowResponseDTO {
    private int followersCount;
    private int followingCount;
    private List<UserSummaryDTO> followers;
    private List<UserSummaryDTO> following;
}