package com.nut.common.exception;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.exception
 * @Author: yzl
 * @CreateTime: 2021-06-16 19:50
 * @Version: 1.0
 */

public class LocalCloudException extends RuntimeException {
    private String message;
    public LocalCloudException(String message){
        super(message);
        this.message = message;
    }

    public LocalCloudException(){
        this.message = "位置云服务器响应异常！";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
