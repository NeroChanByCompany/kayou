package com.nut.driver.app.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.poi.hpsf.Decimal;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author liuBing
 * @Classname ViolationCallbackDto
 * @Description TODO 查询结果返回值
 * @Date 2021/8/20 13:24
 */
@Data
@Accessors(chain = true)
public class ViolationDto implements Serializable {
    private static final long serialVersionUID = 8803093978619584935L;
    /**
     * 违章时间
     */
    private String time;
    /**
     * 违章时间(时间戳)
     */
    private Integer timestamp;
    /**
     * 违章地址
     */
    private String address;
    /**
     * 违章行为
     */
    private String content;
    /**
     * 违法代码
     */
    private Integer legalNum;
    /**
     * 罚款金额
     */
    private BigDecimal price;
    /**
     * 违章记分数
     */
    private Integer score;
    /**
     * 采集交管名称
     */
    private String carorg;
    /**
     * 违章处理状态名称
     */
    private String stateName;
    /**
     * 违章处理状态
     */
    private Integer state;
    /**
     * 违章城市
     */
    private String illegalCity;
    /**
     * 电子监控编号
     */
    private Integer wsbh;
    /**
     * 处罚决定书编号
     */
    private Integer jdsh;
    /**
     * 处理时间
     */
    private String clsj;
    /**
     * 当事人
     */
    private String dsr;
    /**
     * 处理编号（在线处理时，传该编号）
     */
    private String clbh;
    /**
     * 在线处理 （1：可以处理 2：不支持处理 为空或不存在结果时，默认为：不支持处理）
     */
    private Integer zxcl;
    /**
     * 违章条数
     */
    private Integer wzts;
    /**
     * 违章罚款合计
     */
    private BigDecimal wzfkhj;
    /**
     * 违章积分合计
     */
    private Integer wzjfhj;

}
