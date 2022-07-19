package com.nut.common.annotation;

import java.lang.annotation.*;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.annotation
 * @Author: yzl
 * @CreateTime: 2021-06-16 09:13
 * @Version: 1.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface NutTools {
}
