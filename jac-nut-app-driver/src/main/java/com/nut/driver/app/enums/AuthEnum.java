package com.nut.driver.app.enums;

/**
 * @author liuBing
 * @Classname CountEnum
 * @Description TODO 认证是否成功
 * @Date 2021/8/10 16:05
 */
public enum AuthEnum {

    /**
     * 所有月份
     */
    SUCCESS(0,"成功"),
    /**
     * 当前月份
     */
    FAIL(1,"失败"),
    ;


    private Integer code;
    private String value;

    AuthEnum(Integer code, String value){
        this.code = code;
        this.value = value;
    }

    public String getValue(){
        return this.value;
    };

    public Integer getCode(){
        return this.code;
    }

    public static AuthEnum getCodeByType(Integer type){
        for (AuthEnum value : AuthEnum.values()) {
            if (value.code.equals(type)){
                return value;
            }
        }
        return AuthEnum.SUCCESS;
    };
}
