package com.nut.common.result;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.MDC;

/**
 * @description:
 * @author: hcb
 * @createTime: 2021/6/7 20:46
 * @version:1.0
 */
@JsonRootName("result")
@Data
public class HttpCommandResultWithData<T> {

    private int resultCode;
    private String message;
    private T data;
    private String traceId;

    public HttpCommandResultWithData() {
        this.resultCode = ECode.SUCCESS.code();
        this.message = ECode.SUCCESS.message();
        this.traceId = MDC.get("traceId");
    }

    public HttpCommandResultWithData(Integer code, String message) {
        this.resultCode = code;
        this.message = message;
        this.traceId = MDC.get("traceId");
    }

    public HttpCommandResultWithData(Integer code, String message, T data) {
        this.resultCode = code;
        this.message = message;
        this.data = data;
        this.traceId = MDC.get("traceId");
    }
    public T getData() {
        return data;
    }

    public HttpCommandResultWithData setData(T data) {
        this.data = data;
        return this;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
