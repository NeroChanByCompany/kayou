package com.jac.app.job.enums;

/**
 * @author liuBing
 * @Classname StatusEnum
 * @Description TODO 用户是否注销
 * @Date 2021/8/12 17:10
 */
public enum FreezeEnum {
    /**
     * 小时
     */
    HOURS(1,"小时"),
    /**
     * 天
     */
    DAYS(2,"天"),
    /**
     * 周
     */
    WEEK(3,"周");
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
