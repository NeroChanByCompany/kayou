package com.nut.servicestation.app.dto;

import lombok.Data;

/**
 * 车队车辆列表接口返回DTO
 */
@Data
public class TempActionPictureSetDto {
    private Long id;
    private String actionCode;
    private String actionName;
    private String pictureUrl;
    private String order;
    private String inVersion;
    private String outVersion;
    private String describe;
    private String fileSize;
    private Integer flag;
}
