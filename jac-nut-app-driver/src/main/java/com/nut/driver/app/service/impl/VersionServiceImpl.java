package com.nut.driver.app.service.impl;

import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.driver.app.controller.VersionController;
import com.nut.driver.app.form.OperationManualListForm;
import com.nut.driver.app.pojo.VersionPojo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nut.driver.app.dao.VersionDao;
import com.nut.driver.app.entity.VersionEntity;
import com.nut.driver.app.service.VersionService;

import java.util.List;

@Service("versionService")
@Slf4j
public class VersionServiceImpl extends ServiceImpl<VersionDao, VersionEntity> implements VersionService {


    /**
     * 获取用户指南
     */
    public List getUserGuide(OperationManualListForm form) {
        List<VersionPojo> data = null;
        try {
            data = this.baseMapper.queryVersionList(null);
        } catch (Exception e){
            ExceptionUtil.result(ECode.SERVER_ERROR.code(),"获取用户指南列表失败");
            log.error(VersionController.class.getSimpleName() + " Exception:" + e);
        }
        log.info("getUserGuide end return:{}",data);
        return data;
    }
}
