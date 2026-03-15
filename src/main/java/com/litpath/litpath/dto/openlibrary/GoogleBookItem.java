package com.litpath.litpath.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleBookItem {
    private GoogleVolumeInfo volumeInfo;
}