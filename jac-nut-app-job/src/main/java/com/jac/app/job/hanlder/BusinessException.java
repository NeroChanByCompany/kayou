package com.jac.app.job.hanlder;
/**
 * @Classname BizException
 * @Description TODO 全局统一异常处理
 * @Date 2021/8/12 16:15
 * @author liuBing
 */public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 6685069588765265497L;

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
