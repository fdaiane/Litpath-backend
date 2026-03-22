package com.litpath.litpath.dto;

import com.litpath.litpath.model.ListType;
import lombok.Data;

import java.util.List;

@Data
public class UserBookListResponseDTO {
    private Long id;
    private ListType listType;
    private List<BookResponseDTO> books;
}