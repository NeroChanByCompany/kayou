package com.nut.servicestation.app.domain;

import lombok.Data;

@Data
public class DataDict {
    private String id;

    private String code;

    private Integer value;

    private String name;

    private String keyType;

    private Integer patternA;

    private Integer patternB;

    private Integer patternC;

    private Integer patternD;

    private Integer availableFlag;

}