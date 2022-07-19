package com.nut.common.enums;

/**
 * @description: DB记录是否启用标识
 * @author: hcb
 * @createTime: 2020/11/24 10:51
 * @version:1.0
 */
public enum DbDisabledEnum {

    NO(0, "启用"),
    YES(1, "禁用");

    private Integer code;
    private String message;

    DbDisabledEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
