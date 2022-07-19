package com.nut.driver.app.enums;

/**
 * @author liuBing
 * @Classname CountEnum
 * @Description TODO
 * @Date 2021/8/10 16:05
 */
public enum CountEnum {

    /**
     * 所有月份
     */
    ALL(1,"所有月份"),
    /**
     * 当前月份
     */
    NOW(2,"当前月份"),
    /**
     * 上一月份
     */
    BEFORE(3,"上一月份")
    ;


    private Integer code;
    private String value;

    CountEnum(Integer code, String value){
        this.code = code;
        this.value = value;
    }

    public String getValue(){
        return this.value;
    };

    public Integer getCode(){
        return this.code;
    }

    public static CountEnum getCodeByType(Integer type){
        for (CountEnum value : CountEnum.values()) {
            if (value.code.equals(type)){
                return value;
            }
        }
        return CountEnum.NOW;
    };
}
