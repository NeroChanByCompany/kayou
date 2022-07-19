package com.nut.common.result;

import org.slf4j.MDC;

/**
 * @author liuBing
 * @Classname Result
 * @Description TODO 统一响应返回
 * @Date 2021/6/15 22:01
 */
public class Result<T> extends HttpCommandResultWithData {


    private int resultCode;
    private String message;
    private T data;
    private String traceId;


    private Result() {
    }

    public Result(Integer code, String message) {
        super(code, message);
    }

    public Result(Integer code, String message, Object data) {
        super(code, message, data);
    }

    private static HttpCommandResultWithData getResult() {
        return new HttpCommandResultWithData();
    }

    /**
     * 无实际返回体
     *
     * @param code
     * @param message
     * @return
     */
    public static HttpCommandResultWithData result(Integer code, String message) {
        HttpCommandResultWithData result = getResult();
        result.setResultCode(code);
        result.setMessage(message);
        result.setTraceId(MDC.get("traceId"));
        return result;
    }

    /**
     * 有实际返回体
     *
     * @param code
     * @param message
     * @return
     */
    public static HttpCommandResultWithData result(Integer code, String message, Object date) {
        HttpCommandResultWithData result = getResult();
        result.setResultCode(code);
        result.setMessage(message);
        result.setData(date);
        result.setTraceId(MDC.get("traceId"));
        return result;
    }

    /**
     * 消息成功返回-无返回体
     *
     * @return
     */
    public static HttpCommandResultWithData ok() {
        HttpCommandResultWithData result = getResult();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        result.setTraceId(MDC.get("traceId"));
        return result;
    }

    /**
     * 消息成功返回-有返回体
     *
     * @return
     */
    public static HttpCommandResultWithData ok(Object date) {
        HttpCommandResultWithData result = getResult();
        result.setResultCode(ECode.SUCCESS.code());
        result.setMessage(ECode.SUCCESS.message());
        result.setData(date);
        result.setTraceId(MDC.get("traceId"));
        return result;
    }

}
