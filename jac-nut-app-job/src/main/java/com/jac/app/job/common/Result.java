package com.jac.app.job.common;

import com.xxl.job.core.biz.model.ReturnT;
import lombok.Data;
import org.slf4j.MDC;

/**
 * @author liuBing
 * @Classname Result
 * @Description TODO 整体参数返回类
 * @Date 2021/8/12 15:53
 */
@Data
public class Result<T> {
    public static final long serialVersionUID = 42L;
    public static final int SUCCESS_CODE = 200;
    public static final int FAIL_CODE = 500;
    public static final int NETWORK_FAIL_CODE = 615;
    public static final Result<String> SUCCESS = new Result((Object) "操作成功");
    public static final Result<String> FAIL = new Result(500, (String) "服务异常");

    private int resultCode;
    private String message;
    private String traceId;
    private T data;

    public Result() {
    }

    public Result(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
        this.traceId = MDC.get("traceId");
    }

    public Result(T data) {
        this.resultCode = 200;
        this.message = "操作成功";
        this.data = data;
        this.traceId = MDC.get("traceId");
    }

    public Result(int resultCode, String message, T data) {
        this.resultCode = resultCode;
        this.message = message;
        this.data = data;
        this.traceId = MDC.get("traceId");

    }

    public static <T> Result<T> result(int resultCode, String msg) {
        return new Result<T>(resultCode, msg);
    }


    public static <T> Result<T> result(int resultCode, String msg,T data) {
        return new Result<T>(resultCode, msg,data);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<T>(data);
    }


}
