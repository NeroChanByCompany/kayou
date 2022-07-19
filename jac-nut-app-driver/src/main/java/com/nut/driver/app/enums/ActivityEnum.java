package com.nut.driver.app.enums;

/**
 * @author liuBing
 * @Classname ActivityEnum
 * @Description TODO 活动是否开关
 * @Date 2021/5/28 9:22
 */
public enum ActivityEnum {
    /**
     * 活动打开状态
     */
    OPEN(1,"活动开启"),
    /**
     * 活动关闭状态
     */
    SHUT_DOWN(2,"活动关闭");


    private Integer code;
    private String value;

    ActivityEnum(Integer code, String value){
        this.code = code;
        this.value = value;
    }

    public String getValue(){
        return this.value;
    };

    public Integer getCode(){
        return this.code;
    }

}
