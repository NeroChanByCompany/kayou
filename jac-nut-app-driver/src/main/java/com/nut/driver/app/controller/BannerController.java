package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.form.GetBannerInfoForm;
import com.nut.driver.app.service.BannerInfoService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: Banner页面控制器
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-28 15:25
 * @Version: 1.0
 */
@RestController
@Slf4j
public class BannerController extends BaseController {

    @Autowired
    private BannerInfoService bannerInfoService;

    /**
     * @Description：appBanner页面查询
     * @author YZL
     * @data 2021/6/30 15:21
     */
    @PostMapping(value = "/getBannerInfo")
    //@LoginRequired
    @SneakyThrows
    @ApiImplicitParams({
            @ApiImplicitParam(value = "appType", name = "APP类型", dataType = "String")
    })
    public HttpCommandResultWithData getBannerInfo(@ApiIgnore @RequestJson GetBannerInfoForm form) {
        log.info("getBannerInfo start param:{}", form);
        this.formValidate(form);
        return Result.ok(bannerInfoService.getBannerInfoList(form));
    }

}
