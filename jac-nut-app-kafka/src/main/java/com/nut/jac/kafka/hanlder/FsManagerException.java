package com.nut.jac.kafka.hanlder;

/**
 * 文件服务器响应异常
 */
public class FsManagerException extends RuntimeException{

    private String message;
    public FsManagerException(String message){
        super(message);
        this.message = message;
    }

    public FsManagerException(){
        this.message = "文件服务器响应异常！";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
