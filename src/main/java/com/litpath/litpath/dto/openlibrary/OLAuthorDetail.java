package com.litpath.litpath.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OLAuthorDetail {

    private String key;
    private String name;

    @JsonProperty("bio")
    private Object bio;

    @JsonProperty("birth_date")
    private String birthDate;

    @JsonProperty("personal_name")
    private String personalName;
}