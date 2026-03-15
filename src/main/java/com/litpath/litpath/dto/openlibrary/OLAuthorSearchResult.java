package com.litpath.litpath.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OLAuthorSearchResult {
    private List<OLAuthorDoc> docs;
}