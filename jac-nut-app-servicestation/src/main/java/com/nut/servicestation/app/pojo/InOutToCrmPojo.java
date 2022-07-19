package com.nut.servicestation.app.pojo;

import lombok.Data;

/**
 * @author liuBing
 * @Classname InOutToCrmPojo
 * @Description TODO
 * @Date 2021/7/7 19:30
 */
@Data
public class InOutToCrmPojo {
    private String chassisNo;
    private String identifyNum;
    private String inOutTime;
    private String moveClass;
}
