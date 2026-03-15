package com.litpath.litpath.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OLAuthorDoc {

    private String key;
    private String name;

    @JsonProperty("birth_date")
    private String birthDate;

    @JsonProperty("top_subjects")
    private List<String> topSubjects;
}