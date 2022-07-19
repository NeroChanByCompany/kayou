package com.jac.app.job.enums;

/**
 * @author liuBing
 * @Classname StatusEnum
 * @Description TODO 用户是否注销
 * @Date 2021/8/12 17:10
 */
public enum WorkOrderLogEnum {
    /**
     * 推送成功
     */
    NORMAL(0,"推送成功"),
    /**
     * 推送失败
     */
    CANCELLATION(1,"推送失败");


    private Integer code;
    private String value;

    WorkOrderLogEnum(Integer code, String value){
        this.code = code;
        this.value = value;
    }

    public String getValue(){
        return this.value;
    };

    public Integer getCode(){
        return this.code;
    }

    public static WorkOrderLogEnum getCodeByType(Integer type){
        for (WorkOrderLogEnum value : WorkOrderLogEnum.values()) {
            if (value.code.equals(type)){
                return value;
            }
        }
        return WorkOrderLogEnum.NORMAL;
    };
}
