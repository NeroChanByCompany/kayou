package com.jac.app.job.enums;

/**
 * @author liuBing
 * @Classname StatusEnum
 * @Description TODO 用户是否注销
 * @Date 2021/8/12 17:10
 */
public enum StatusEnum {
    /**
     * 激活状态
     */
    NORMAL(0,"激活状态"),
    /**
     * 注销状态
     */
    CANCELLATION(1,"注销状态"),
    /**
     * 冻结状态
     */
    FREEZE(2,"冻结");
    private Integer code;
    private String value;

    StatusEnum(Integer code, String value){
        this.code = code;
        this.value = value;
    }

    public String getValue(){
        return this.value;
    };

    public Integer getCode(){
        return this.code;
    }

    public static StatusEnum getCodeByType(Integer type){
        for (StatusEnum value : StatusEnum.values()) {
            if (value.code.equals(type)){
                return value;
            }
        }
        return StatusEnum.NORMAL;
    };
}
