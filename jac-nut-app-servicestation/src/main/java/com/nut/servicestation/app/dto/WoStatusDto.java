package com.nut.servicestation.app.dto;


import lombok.Data;

import java.io.Serializable;

@Data
public class WoStatusDto implements Serializable {
    private String woCode;
    private Integer woStatus;
    private Integer maximumTime;

}
