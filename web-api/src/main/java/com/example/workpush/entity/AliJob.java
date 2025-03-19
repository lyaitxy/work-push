package com.example.workpush.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AliJob {
    private String id;
    private String name;
    private String categoryType;
    private List<String> workLocations;
    private String requirement;
    private String description;
}
