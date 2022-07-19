package com.nut.common.annotation;

import com.nut.common.validator.EmojiForbidValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Description: 拒绝Emoji
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.annotation
 * @Author: yzl
 * @CreateTime: 2021-06-21 19:20
 * @Version: 1.0
 */
@Constraint(validatedBy = EmojiForbidValidator.class) //具体的实现
@Target( { java.lang.annotation.ElementType.METHOD,
        java.lang.annotation.ElementType.FIELD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface EmojiForbid {
    String message() default "不能输入Emoji表情";

    //下面这两个属性必须添加
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};


}
