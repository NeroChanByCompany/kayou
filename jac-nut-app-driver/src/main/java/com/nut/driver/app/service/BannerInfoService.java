package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.driver.app.entity.BannerInfoEntity;
import com.nut.driver.app.form.GetBannerInfoForm;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 15:29:42
 */
public interface BannerInfoService extends IService<BannerInfoEntity> {
    List getBannerInfoList(GetBannerInfoForm form);
}

