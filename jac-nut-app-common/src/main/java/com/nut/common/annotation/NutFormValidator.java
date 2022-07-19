package com.nut.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: 参数校验注解
 * @author: hcb
 * @createTime: 2021/02/05 16:58
 * @version:1.0
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface NutFormValidator {

}
