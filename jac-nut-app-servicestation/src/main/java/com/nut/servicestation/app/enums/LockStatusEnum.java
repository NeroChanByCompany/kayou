package com.nut.servicestation.app.enums;

/**
 * @author liuBing
 * @Classname LockStatusEnum
 * @Description TODO
 * @Date 2021/8/18 15:33
 */
public enum LockStatusEnum {

    /**
     * 启用
     */
    ACTIVATION(0,"启用"),
    /**
     * 停用
     */
    SHUT_DOWN(1,"停用");


    private Integer code;
    private String value;

    LockStatusEnum(Integer code, String value){
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
