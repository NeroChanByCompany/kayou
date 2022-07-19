package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.driver.app.dto.PopConfigurationDto;
import com.nut.driver.app.entity.AppPopupConfigurationEntity;

import java.util.Map;
import java.util.Optional;

/**
 *
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 09:40:50
 */
public interface AppPopupConfigurationService extends IService<AppPopupConfigurationEntity> {

    Optional<PopConfigurationDto> queryPopConfigurationNew();

}

