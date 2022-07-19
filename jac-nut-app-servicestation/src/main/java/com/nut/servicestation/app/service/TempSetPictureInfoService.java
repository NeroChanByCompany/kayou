package com.nut.servicestation.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.servicestation.app.form.GetSetPictureInfoForm;

/*
 *  @author wuhaotian 2021/7/7
 */
public interface TempSetPictureInfoService {
    /**
     * 获取设置的图片
     * @param command
     * @return
     */
    HttpCommandResultWithData getPictureInfoList(GetSetPictureInfoForm command);
}
