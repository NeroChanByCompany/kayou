package com.nut.common.domain;

import lombok.Data;

import java.io.Serializable;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.domain
 * @Author: yzl
 * @CreateTime: 2021-06-16 19:47
 * @Version: 1.0
 */
@Data
public class FsManagerGenericResponse<T> implements Serializable {

    private int status;

    private String message;

    private T data;

}
