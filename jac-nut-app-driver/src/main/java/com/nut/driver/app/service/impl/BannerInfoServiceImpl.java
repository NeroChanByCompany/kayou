package com.nut.driver.app.service.impl;

import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.driver.app.dto.BannerInfoDto;
import com.nut.driver.app.form.GetBannerInfoForm;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nut.driver.app.dao.BannerInfoDao;
import com.nut.driver.app.entity.BannerInfoEntity;
import com.nut.driver.app.service.BannerInfoService;

import java.util.List;

@Service("bannerInfoService")
@Slf4j
public class BannerInfoServiceImpl extends ServiceImpl<BannerInfoDao, BannerInfoEntity> implements BannerInfoService {

    public List getBannerInfoList(GetBannerInfoForm form) {
        List<BannerInfoDto> list = null;
        try {
            if (StringUtils.isBlank(form.getAppType())) {
                ExceptionUtil.result(ECode.PARAM_FAIL.code(), "请求参数异常");
            }
            list = this.baseMapper.getBannerInfoForApp(form.getAppType());
        } catch (Exception e) {
            log.error("GetBannerInfo:", e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "获取首页轮播广告失败");
        }
        log.info("getBannerInfo end return:{}", list);
        return list;
    }
}
