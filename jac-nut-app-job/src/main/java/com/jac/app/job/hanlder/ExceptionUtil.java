package com.jac.app.job.hanlder;

/**
 * @author liuBing
 * @Classname ExceptionUtil
 * @Description TODO
 * @Date 2021/6/16 11:02
 */
public class ExceptionUtil {
    private ExceptionUtil(){}

    /**
     *  自定义异常结果抛出
     * @param code 状态码
     * @param message 异常信息
     */
    public static void result(Integer code,String message){
        throw new BusinessException(code,message);
    }
}
