package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.driver.app.entity.ActionPictureSetEntity;
import com.nut.driver.app.form.GetSetPictureInfoForm;
import com.nut.driver.app.pojo.AppVersion;

import java.util.List;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-18 13:35:38
 */
public interface ActionPictureSetService extends IService<ActionPictureSetEntity> {

    /**
     *
     * @param form
     * @return
     */
    List getVersionMessage(GetSetPictureInfoForm form);

    /**
     * 查询当前app版本
     * @param version
     * @return
     */
    ActionPictureSetEntity getVersion(AppVersion version);
}

