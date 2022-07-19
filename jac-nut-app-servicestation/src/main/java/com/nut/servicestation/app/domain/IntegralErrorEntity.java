package com.nut.servicestation.app.domain;


import lombok.Data;

import java.sql.Timestamp;

@Data
public class IntegralErrorEntity {
    private String userId;
    private String interfaceParam;
    private String interfaceName;
    private String errorInfo;
    private Timestamp errTime;

}
