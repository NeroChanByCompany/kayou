package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.form.ViolationForm;
import com.nut.driver.app.service.ThirdPartyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author liuBing
 * @Classname ThirdPartyController
 * @Description TODO 调用第三方接口
 * @Date 2021/8/20 13:08
 */
@Api(tags = "调用第三方接口")
@Slf4j
@RestController
@RequestMapping("/thirdParty")
public class ThirdPartyController extends BaseController {

    @Autowired
    ThirdPartyService thirdPartyService;

    /**
     * 调用第三方接口查询车辆位置信息
     * @return 违章信息结果
     */
    @SneakyThrows
    @ApiImplicitParams({
            @ApiImplicitParam(name = "type" , value = "车辆类型" , dataType = "String"),
            @ApiImplicitParam(name = "plateno" , value = "车牌号" , dataType = "String"),
            @ApiImplicitParam(name = "engineno" , value = "发动机号" , dataType = "String"),
            @ApiImplicitParam(name = "frameno" , value = "车架号" , dataType = "String"),
    })
    //@LoginRequired
    @PostMapping("/queryViolation")
    public HttpCommandResultWithData queryViolation(@ApiIgnore @RequestBody ViolationForm form){
        log.info("ThirdPartyController queryViolation start form:{}",form);
        this.formValidate(form);
        return Result.ok(thirdPartyService.queryViolation(form));
    }
}
