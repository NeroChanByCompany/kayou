package com.nut.servicestation.app.dto;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * 工单上报图片对象
 */
@Data
public class OrderReportPhotoDto implements Serializable {
    /**
     * 图片名称
     */
    @JSONField(name = "IMG_NAME")
    private String IMG_NAME;

    /**
     * 图片地址
     */
    @JSONField(name = "IMG_URL")
    private String IMG_URL;

    /**
     * 图片64位编码
     */
    @JSONField(name = "BASE64_STR")
    private String BASE64_STR;

}
