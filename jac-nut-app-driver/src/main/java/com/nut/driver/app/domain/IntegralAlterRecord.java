package com.nut.driver.app.domain;


import lombok.Data;

import java.util.Date;

@Data
public class IntegralAlterRecord {
    private Long id;
    private String uid;
    private String credits;
    private String type;
    private String integralItem;
    private String integralResource;
    private String orderNum;
    private Date createTime;
    private Date updateTime;
    private Integer balance;

    @Override
    public String toString() {
        return "IntegralAlterRecord{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", credits='" + credits + '\'' +
                ", type='" + type + '\'' +
                ", integralItem='" + integralItem + '\'' +
                ", integralResource='" + integralResource + '\'' +
                ", orderNum='" + orderNum + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
