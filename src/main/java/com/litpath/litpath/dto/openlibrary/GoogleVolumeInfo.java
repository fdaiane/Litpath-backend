package com.litpath.litpath.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleVolumeInfo {
    private String title;
    private String description;
    private String publishedDate;
    private List<String> categories;
    private GoogleImageLinks imageLinks;
}