package com.example.common.enums;

import com.example.entity.Records;
import com.example.service.RecordsService;

public enum RecordsEnum {
    OUT("支出"),
    CHARGE("充值"),
    INCOME("骑手"),

    CANCEL("取消");

    public String getValue() {
        return value;
    }

    private String value;

      RecordsEnum(String value){
        this.value =value;

    }
}
