package com.example.workpush.jobEnum.MeiTuan;

import lombok.Getter;

@Getter
public enum MeiTuanCategoryType {

    YJ("应届校招","freshman"),
    SX("暑期实习","internship"),
    RC("日常实习","project");

    private final String type;
    private final String desc;

    MeiTuanCategoryType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static String getValue(String key) {
        for (MeiTuanCategoryType item : values()) {
            if (item.desc.equals(key)) {
                return item.type;
            }
        }
        return "未知键";
    }
}
