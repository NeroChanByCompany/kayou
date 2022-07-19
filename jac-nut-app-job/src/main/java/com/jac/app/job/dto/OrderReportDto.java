package com.jac.app.job.dto;

import lombok.Data;

import java.util.List;

/**
 * 订单上报对象
 */
@Data
public class OrderReportDto {

    /**
     * 订单图片对象
     */
    private List<OrderReportPhotoDto> checkImgList;

}
