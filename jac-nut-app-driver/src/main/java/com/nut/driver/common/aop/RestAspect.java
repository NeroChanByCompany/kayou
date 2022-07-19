package com.nut.driver.common.aop;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nut.common.exception.BusinessException;
import com.nut.common.exception.ParamException;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.common.component.TokenComponent;
import com.nut.driver.common.utils.ReqUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Objects;

/**
 * @description: Rest请求切面组件
 * @author: hcb
 * @createTime: 2021/5/4 16:09
 * @version:1.0
 */
@Aspect
@Component
@Slf4j
@Order(1)
public class RestAspect {

    @Resource
    TokenComponent tokenComponent;

    /**
     * 不需要日志输出的接口url
     * TODO，根据实际情况进行增减
     */
    private static final String[] NOT_LOG_URL = {"/swagger-ui.html", "/webjars", "/swagger-resources", "/api-docs", "/csrf", "/doc.html", "/export",
            "/druid"};

    /**
     * 定义切入点 就是需要拦截的切面
     */
    @Pointcut("execution(public * com.nut.driver.app.controller.*.*(..))")
    public void controllerMethod() {
    }

    /**
     * 在进入controller之前拦截并打印请求报文日志
     *
     * @param joinPoint
     * @throws Exception
     */
    @Before("controllerMethod()")
    public void logRequestInfo(JoinPoint joinPoint) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 判断是否需要日志
        Boolean needLog = true;
        String uri = request.getRequestURI();
        for (String tmp : NOT_LOG_URL) {
            if (uri.contains(tmp)) {
                needLog = false;
                break;
            }
        }
        if (!needLog) {
            return;
        }

        // 处理请求参数
        Signature signature = joinPoint.getSignature();
        String[] paramNames = ((MethodSignature) signature).getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        Object[] args = joinPoint.getArgs();


        String contentType = StringUtils.trimToEmpty(request.getContentType());
        String method = request.getMethod();
        String reqBody = "";
        if (contentType.contains("multipart")) {
            reqBody = "file data";
        } else {
            int paramLength = paramValues.length;
            if ("GET".equalsIgnoreCase(method)) {
                for (int i = 0; i < paramLength; i++) {
                    reqBody += "&" + paramNames[i] + "=" + paramValues[i];
                }
                reqBody = reqBody.replaceFirst("&", "");
            } else {
                if (0 == paramLength) {
                    reqBody = "{}";
                } else if (1 == paramLength) {
                    reqBody = JSONObject.toJSONString(paramValues[0]);
                } else {
                    reqBody = JSONObject.toJSONString(paramValues);
                }
            }
        }

        String token = ReqUtils.getRequestHeader("token");
        String phone = "";
        if (StringUtils.isNotBlank(token)) {
            Object oldPhone = tokenComponent.getTokenInfo(token).get("phone");
            if (Objects.nonNull(oldPhone)) {
                phone = String.valueOf(oldPhone);
            }
        }
        log.info("当前请求账户:{}", phone);
        // 日志输出请求体
        log.info("请求参数：{}", reqBody);

        // 日志输出请求头
        this.logReqHeader(request);
    }

    /**
     * 返回信息后，打印应答报文的日志
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("controllerMethod()")
    public Object printResponseDatagram(ProceedingJoinPoint joinPoint) {
        try {
            Object result = joinPoint.proceed();
            String resBody = null;
            if (Objects.nonNull(result)) {
                resBody = JSONObject.toJSONString(result);
            }
            log.info("请求响应：{}", resBody);
            return result;
        } catch (Throwable e) {
            HttpCommandResultWithData response;
            if (e instanceof ParamException) {
                response = new HttpCommandResultWithData(ECode.PARAM_ERROR.code(), ((ParamException) e).getErrorMessage());
            } else if (e instanceof BusinessException) {
                response = new HttpCommandResultWithData(((BusinessException) e).getCode(), ((BusinessException) e).getErrorMessage());
            } else {
                log.error("当前异常:",e);
                StackTraceElement[] stackTrace = e.getStackTrace();
                if (Objects.nonNull(stackTrace) && stackTrace.length > 0) {
                    for (StackTraceElement stackTraceElement : stackTrace) {
                        log.error("异常详细信息：{}", stackTraceElement);
                    }
                }
                response = new HttpCommandResultWithData(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message(), e.getLocalizedMessage());
            }
            log.info("请求响应：{}", JSON.toJSONString(response));
            return response;
        }
    }

    /**
     * 日志输出请求头
     **/
    private void logReqHeader(HttpServletRequest request) {
        JSONObject headerJson = new JSONObject();
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            if ("token".equals(key) || "Content-Type".equalsIgnoreCase(key)) {
                headerJson.put(key, request.getHeader(key));
            }
        }
        log.info("请求头：{}", headerJson.toJSONString());
    }
}
