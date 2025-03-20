package com.example.workpush.jobEnum.Ali;

import lombok.Getter;

@Getter
public enum CategoryType {
    YJ("freshman", "应届校招"),
    SX("internship", "暑期实习"),
    RC("project", "日常实习");

    private final String type;
    private final String desc;

    CategoryType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static String getValue(String key) {
        for (CategoryType item : values()) {
            if (item.type.equals(key)) {
                return item.desc;
            }
        }
        return "未知键";
    }
}
