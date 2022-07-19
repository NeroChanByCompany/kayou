package com.nut.jac.kafka.hanlder;



import com.nut.jac.kafka.common.Result;
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
    public Result<String> handleBindException(Throwable e) {
        e.printStackTrace();
        return Result.FAIL;
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public Result<String> handleBindException( BindException e) {
        e.printStackTrace();
        return Result.FAIL;
    }

    @ExceptionHandler(FsManagerException.class)
    @ResponseBody
    public Result<String> handleBindException(FsManagerException e) {
        e.printStackTrace();
        return Result.FAIL;
    }
    /**
     * 处理业务异常
     *
     * @param e 异常栈
     * @return HttpCommandResultWithData
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public Result<String> handleException(BusinessException e) {
        e.printStackTrace();
        return Result.result(e.getCode(),e.getErrorMessage());
    }
}
