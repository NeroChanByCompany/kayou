package com.nut.common.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @description: List size
 * @author: hcb
 * @createTime: 2021/02/05 16:58
 * @version:1.0
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SizeValidator {

    int minSize() default 0;

    int maxSize() default 999999;

    String message() default "";
}
