package com.jac.app.job.hanlder;

/**
 * @description: 参数错误异常
 * @author: hcb
 * @createTime: 2021/01/20 17:26
 * @version:1.0
 */
public class ParamException extends RuntimeException {

    private String errorMessage;

    public ParamException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
