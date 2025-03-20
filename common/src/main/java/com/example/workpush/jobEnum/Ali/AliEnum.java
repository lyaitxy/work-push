package com.example.workpush.jobEnum.Ali;

import lombok.Getter;

@Getter
public enum AliEnum {
    GD("talent.amap.com", "高德"),
    ELM("talent.ele.me","饿了吗"),
    ALY("careers.aliyun.com", "阿里云"),
    TT("talent.taotian.com","淘天"),
    ALGJ("aidc-jobs.alibaba.com", "阿里国际"),
    CN("talent.cainiao.com", "菜鸟"),
    ALDWY("jobs.alibaba-dme.com", "阿里大文娱")
    ;

    private final String english;
    private final String chinese;

    AliEnum(String english, String chinese) {
        this.english = english;
        this.chinese = chinese;
    }

    public static String getValue(String key) {
        for (AliEnum item : values()) {
            if (item.english.equals(key)) {
                return item.chinese;
            }
        }
        return "未知键";
    }
}
