package com.nut.driver.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class HttpInterceptor implements ClientHttpRequestInterceptor {

    private Logger log = LoggerFactory.getLogger(HttpInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        log.info("请求地址：{}", request.getURI());
        log.info("请求方法： {}", request.getMethod());
        log.info("请求内容：{}", new String(body));
        log.info("请求头：{}", request.getHeaders());
        return execution.execute(request, body);
    }
}