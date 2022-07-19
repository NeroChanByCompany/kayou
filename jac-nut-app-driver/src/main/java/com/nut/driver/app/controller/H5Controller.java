package com.nut.driver.app.controller;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.form.InviteIntegralForm;
import com.nut.driver.app.service.H5Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: H5页面调用接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-11-17 14:36
 * @Version: 1.0
 */
@RestController
@RequestMapping("/h5")
@Api(value = "h5页面调用")
@Slf4j
public class H5Controller {

    @Autowired
    private H5Service h5Service;

    @PostMapping(value = "/inviteIntegral")
    @SneakyThrows
    @ApiOperation(value = "商城H5页面")
    public HttpCommandResultWithData inviteIntegral(@RequestBody InviteIntegralForm form){
        log.info("规则id:{}", form);
        return h5Service.inviteIntegral(form);
    }

}
