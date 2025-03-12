package com.example.workpush.pojo.entity;

import com.example.workpush.utils.TimestampToLocalDateTimeDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GaoDeWork {
    // 岗位名称
    String name;
    // 毕业要求
    GraduationTime graduationTime;
    // 更新时间
    @JsonDeserialize(using = TimestampToLocalDateTimeDeserializer.class)
    LocalDateTime modifyTime;
    // 工作地点
    List<String> workLocations;
    // 要求
    String requirement;
    // 描述
    String description;
    // 岗位类型，internship, freshman
    String categoryType;

    @Data
    static class GraduationTime {
        @JsonDeserialize(using = TimestampToLocalDateTimeDeserializer.class)
        LocalDateTime from;
        @JsonDeserialize(using = TimestampToLocalDateTimeDeserializer.class)
        LocalDateTime to;
    }
}
