package com.nut.servicestation.app.form;


import com.nut.common.annotation.NutFormValidator;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @Description: 图片上传接口
 */
@Data
@NutFormValidator
public class UploadPhotoInfoForm {

    @NotNull(message = "工单号不能为空！")
    @NotBlank(message = "工单号不能为空！")
    private String woCode;

    @NotNull(message = "维修记录ID不能为空！")
    @NotBlank(message = "维修记录ID不能为空！")
    private String operateId;

    @NotNull(message = "图片类型不能为空！")
    @NotBlank(message = "图片类型不能为空！")
    private String type;

    @NotNull(message = "时间戳不能为空!")
    private Long timestamp;

    private String lon;
    private String lat;
    private String addr;
    private String deviceNo;

    private String md5;
    private String size;

    private String offlineFlag;

    private String imgURL;

}
