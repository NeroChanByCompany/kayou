package com.nut.servicestation.common.pojo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * @description:
 * @author: hcb
 * @createTime: 2021/01/25 20:21
 * @version:1.0
 */
@Data
public class BaiduMapServicePojo {

    /**
     * 请求的项目：如TSP、TBOSS
     */
    private String project;
    /**
     * 请求百度方式：get或者post
     */
    private String method;
    /**
     * 请求百度的url：原样附上百度url，去掉ak内容
     */
    private String url;
    /**
     * post请求时参数
     */
    private JSONObject params;

    public BaiduMapServicePojo() {
        this.project = "TBOSS";
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
