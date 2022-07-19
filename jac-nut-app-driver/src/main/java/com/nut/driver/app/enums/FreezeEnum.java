package com.nut.driver.app.enums;

/**
 * @author liuBing
 * @Classname StatusEnum
 * @Description TODO 用户是否注销
 * @Date 2021/8/12 17:10
 */
public enum FreezeEnum {
    /**
     * 激活状态
     */
    TWO_HOURS(1,"两小时"),
    /**
     * 注销状态
     */
    SEVEN_DAYS(2,"7天"),
    /**
     * 冻结状态
     */
    FOREVER(3,"永久冻结");
    private Integer code;
    private String value;

    FreezeEnum(Integer code, String value){
        this.code = code;
        this.value = value;
    }

    public String getValue(){
        return this.value;
    };

    public Integer getCode(){
        return this.code;
    }

    public static FreezeEnum getCodeByType(Integer type){
        for (FreezeEnum value : FreezeEnum.values()) {
            if (value.code.equals(type)){
                return value;
            }
        }
        return null;
    };
}
