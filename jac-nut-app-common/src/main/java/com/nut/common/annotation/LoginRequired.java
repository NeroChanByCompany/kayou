package com.nut.common.annotation;

import java.lang.annotation.*;

/**
 * @description:
 * @author: hcb
 * @createTime: 2021/02/05 16:58
 * @version:1.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {

    boolean value() default true;

}
