package com.jac.app.job.enums;

import java.util.concurrent.TimeUnit;

/**
 * @author liuBing
 * @Classname TimeFormatEnum
 * @Description TODO
 * @Date 2021/10/11 13:17
 */
public enum TimeFormatEnum {
    /**
     * 时
     */
    HOUR(1, "小时"),
    /**
     * 天
     */
    DAY(2,"天"),
    /**
     * 周
     */
    WEEK(3,"永久")
    ;

    /**
     * 状态码
     */
    private Integer code;
    /**
     * value值
     */
    private String value;

    TimeFormatEnum(Integer code, String value){
        this.code = code;
        this.value = value;
    }

    public String getValue(){
        return this.value;
    };

    public Integer getCode(){
        return this.code;
    }

    public static String getValueByCode(Integer code){
        for (TimeFormatEnum value : TimeFormatEnum.values()) {
            if (value.code.equals(code)){
                return value.value;
            }
        }
        return null;
    };
}
