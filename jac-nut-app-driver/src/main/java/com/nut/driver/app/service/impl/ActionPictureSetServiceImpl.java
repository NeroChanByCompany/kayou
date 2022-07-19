package com.nut.driver.app.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nut.driver.app.dto.ActionPictureSetDTO;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.driver.app.form.GetSetPictureInfoForm;
import com.nut.driver.app.pojo.AppVersion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nut.driver.app.dao.ActionPictureSetDao;
import com.nut.driver.app.entity.ActionPictureSetEntity;
import com.nut.driver.app.service.ActionPictureSetService;

import java.util.List;

@Service("actionPictureSetService")
@Slf4j
public class ActionPictureSetServiceImpl extends ServiceImpl<ActionPictureSetDao, ActionPictureSetEntity> implements ActionPictureSetService {

    /**
     * 获取版本升级信息
     */
    @Override
    public List getVersionMessage(GetSetPictureInfoForm form) {
        List<ActionPictureSetDTO> list = null;
        try {
            if (form.getType() == null) {
                form.setType("1");
            }
            list = this.baseMapper.getSetPictureInfo(form.getActionCode(), form.getType());
        } catch (Exception e) {
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "获取版本升级信息失败");
        }
        log.info("getAppVersion end return:{}", list);
        return list;
    }

    @Override
    public ActionPictureSetEntity getVersion(AppVersion version) {

        return getOne(new LambdaQueryWrapper<ActionPictureSetEntity>()
                .eq(ActionPictureSetEntity::getActionCode, version.getActionCode())
                .eq(ActionPictureSetEntity::getType,version.getType()));
    };
}
