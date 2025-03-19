package com.example.workpush.entity.resp;

import lombok.Data;

import java.util.List;

@Data
public class AliJobResp {
    private String name;
    private String categoryType;
    private List<String> workLocations;
    private String requirement;
    private String description;
}
