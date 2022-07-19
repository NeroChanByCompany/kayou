package com.nut.driver.app.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class FsManagerGenericResponse<T> implements Serializable{

    private static final long serialVersionUID = -825159923420895201L;
    private int status;

    private String message;

    private T data;
}
