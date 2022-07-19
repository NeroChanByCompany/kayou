package com.nut.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description:
 * @author: hcb
 * @createTime: 2021/02/05 16:58
 * @version:1.0
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Length {

    int minLength() default 0;

    int maxLength() default 0;

    String message() default "";
}
