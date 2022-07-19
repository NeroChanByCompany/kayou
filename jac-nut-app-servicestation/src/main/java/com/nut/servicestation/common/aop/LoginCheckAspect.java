package com.nut.servicestation.common.aop;


import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseForm;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.common.component.TokenComponent;
import com.nut.servicestation.common.utils.ReqUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @description: Rest请求登录校验切面组件
 * @author: hcb
 * @createTime: 2021/6/8 11:02
 * @version:1.0
 */
@Aspect
@Component
@Slf4j
@Order(2)
public class LoginCheckAspect {

    @Autowired
    private TokenComponent tokenComponent;
    /**
     * 定义切入点 就是需要拦截的切面
     */
    @Pointcut("execution(public * com.nut.servicestation.app.controller.*.*(..))")
    public void controllerMethod() {
    }

    /**
     * 在进入controller之前拦截并设置校验是否登录
     *
     * @param joinPoint
     */
    @Around("controllerMethod()")
    public <T extends BaseForm> Object logRequestInfo(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = methodSignature.getMethod();
        LoginRequired loginRequired = targetMethod.getAnnotation(LoginRequired.class);
        if (Objects.nonNull(loginRequired)) {
            Boolean checkResult = false;
            BaseForm form = null;
            Object[] args = joinPoint.getArgs();

            if (args.length > 0 && args[0] instanceof BaseForm) {
                form = (BaseForm) args[0];
            }

            String token = ReqUtils.getRequestHeader("token");
            if (StringUtils.isBlank(token)){
                if (form != null){
                    if (StringUtils.isNotBlank(form.getToken())){
                        token = form.getToken();
                    }
                }
            }
            if (StringUtils.isNotEmpty(token)) {
                checkResult = tokenComponent.checkToken(token, form);
            }
            if (checkResult) {
                return joinPoint.proceed();
            } else {
                HttpCommandResultWithData response = new HttpCommandResultWithData(ECode.TOKEN_INVALID.code(), ECode.TOKEN_INVALID.message());
                return response;
            }
        }

        return joinPoint.proceed();
    }
}
