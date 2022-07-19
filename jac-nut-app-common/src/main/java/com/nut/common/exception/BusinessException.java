package com.nut.common.exception;

/**
 * @description: 业务失败异常
 * @author: hcb
 * @createTime: 2021/01/20 17:26
 * @version:1.0
 */
public class BusinessException extends RuntimeException {

    private String errorMessage;
    private Integer code;

    public BusinessException(Integer code,String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
