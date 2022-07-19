package com.nut.driver.app.enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 总成类别
 */
public enum SumTypeEnum {


    POWER(1, "动力总成"),

    CHASSIS(2, "底盘部分"),

    BODYWORK(3, "车身及电器");


    private int code;
    private String message;

    SumTypeEnum(final int code, final String message) {
        this.code = code;
        this.message = message;
    }

    public static String getMessage(int code) {
        for (SumTypeEnum sumTypeEnum : SumTypeEnum.values()) {
            if (sumTypeEnum.code() == code) {
                return sumTypeEnum.message;
            }
        }
        return null;
    }

    public static int getCode(String message) {
        for (SumTypeEnum sumTypeEnum : SumTypeEnum.values()) {
            if (sumTypeEnum.message().equals(message)) {
                return sumTypeEnum.code;
            }
        }
        return 0;
    }

    public static List<Map<String,String>> getList() {
        List<Map<String,String>> list=new ArrayList<>();
        for (SumTypeEnum sumTypeEnum : SumTypeEnum.values()) {
            Map<String,String> map=new HashMap<>();
            map.put("code",sumTypeEnum.code+"");
            map.put("message",sumTypeEnum.message);
            list.add(map);
        }
        return list;
    }

    public int code() {
        return code;
    }

    public String message() {
        return message;
    }
}
