package com.nut.common.exception;

import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class HandlerException {

    /**
     * 处理未知异常
     * @param e
     * @return
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public HttpCommandResultWithData<String> handleBindException(Throwable e) {
        e.printStackTrace();
        return Result.result(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public HttpCommandResultWithData<String> handleBindException( BindException e) {
        e.printStackTrace();
        return Result.result(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());
    }

    @ExceptionHandler(FsManagerException.class)
    @ResponseBody
    public HttpCommandResultWithData<String> handleBindException(FsManagerException e) {
        e.printStackTrace();
        return Result.result(ECode.BUSINESS_FAILURE.code(), e.getMessage());
    }
    /**
     * 处理业务异常
     *
     * @param e 异常栈
     * @return HttpCommandResultWithData
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public HttpCommandResultWithData<String> handleException(BusinessException e) {
        HttpCommandResultWithData<String> commandResultWithData = new HttpCommandResultWithData<>();
        commandResultWithData.setMessage(e.getMessage());
        commandResultWithData.setResultCode(e.getCode());
        e.printStackTrace();
        return commandResultWithData;
    }
}
