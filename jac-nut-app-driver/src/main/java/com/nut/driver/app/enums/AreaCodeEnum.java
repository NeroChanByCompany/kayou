package com.nut.driver.app.enums;

/**
 * @author liuBing
 * @Classname AreaCodeEnum
 * @Description TODO
 * @Date 2021/10/22 14:49
 */
public enum AreaCodeEnum {

    /**
     * 上海
     */
    SHANGHAI("31","上海"),
    /**
     * 北京
     */
    BEIJING("11","北京"),
    /**
     * 天津
     */
    TIANJIN("12","天津"),
    /**
     * 重庆
     */
    CHONGQING("50","重庆"),
    UNKNOWN("99","未知");


    private String code;
    private String value;

    AreaCodeEnum(String code, String value){
        this.code = code;
        this.value = value;
    }

    public String getValue(){
        return this.value;
    };

    public String getCode(){
        return this.code;
    }

    public static AreaCodeEnum getTypebyCode(String code){
        for (AreaCodeEnum value : AreaCodeEnum.values()) {
            if (code.equals(value.code)){
                return value;
            }
        }
        return AreaCodeEnum.UNKNOWN;
    }
}
