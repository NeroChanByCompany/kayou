package com.nut.servicestation.app.domain;

import lombok.Data;

@Data
public class AccessoriesCode {
    private Long id;

    private String woCode;

    private String acceCode;

    private String acceType;

    private String operateId;

}