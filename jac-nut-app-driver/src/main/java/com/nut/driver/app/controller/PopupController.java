package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.dto.PopConfigurationDto;
import com.nut.driver.app.form.PopupConfigForm;
import com.nut.driver.app.service.AppPopupConfigurationService;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Optional;

/**
 * @Description: 弹屏相关
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-28 09:33
 * @Version: 1.0
 */
@RestController
@RequestMapping(value = "popConfiguration")
@Slf4j
public class PopupController {

    @Autowired
    private AppPopupConfigurationService appPopupConfigurationService;

    @PostMapping(value = "/search")
    @ApiOperation("弹出广告")
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData getConfig(@ApiIgnore @RequestJson PopupConfigForm form){
        log.info("getConfig start param:{}",form);
        try {
            Optional<PopConfigurationDto> popConfigurationDto = appPopupConfigurationService.queryPopConfigurationNew();
            if (popConfigurationDto.isPresent()) {
                return Result.ok(popConfigurationDto.get());
            }
        } catch (Exception e) {
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "获取配置失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("getConfig end return:{null}");
        return Result.ok();
    }
}
