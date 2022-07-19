package com.nut.driver.app.service.impl;

import com.nut.driver.app.dto.PopConfigurationDto;
import com.nut.driver.app.pojo.PopConfigurationPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.nut.driver.app.dao.AppPopupConfigurationDao;
import com.nut.driver.app.entity.AppPopupConfigurationEntity;
import com.nut.driver.app.service.AppPopupConfigurationService;

import java.util.List;
import java.util.Optional;

@Service("appPopupConfigurationService")
@Slf4j
public class AppPopupConfigurationServiceImpl extends ServiceImpl<AppPopupConfigurationDao, AppPopupConfigurationEntity> implements AppPopupConfigurationService {

    /**
     * 查询当前可以用的弹屏页
     */
    public Optional<PopConfigurationDto> queryPopConfigurationNew() {
        PopConfigurationDto popConfigurationDto = new PopConfigurationDto();
        List<PopConfigurationPojo> list = this.baseMapper.queryPopConfigurationByNew();
        if (list == null || list.size() == 0 || list.get(0) == null) {
            return Optional.empty();
        } else {
            BeanUtils.copyProperties(list.get(0), popConfigurationDto);
            return Optional.of(popConfigurationDto);
        }
    }
}
