package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.form.*;
import com.nut.driver.app.service.ActiveService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 活动接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-29 10:33
 * @Version: 1.0
 */
@RestController
@Slf4j
@RequestMapping(value = "/active")
@Api(tags = "活动接口")
public class ActiveController extends BaseController {

    @Autowired
    private ActiveService activeService;
    @Value("${activity_check:false}")
    private boolean activityCheck;

    /**
     * @Description：是否可以参加最美卡车节活动（当前已弃用）
     * @author YZL
     * @data 2021/6/29 10:39
     */
    @PostMapping(value = "/1/check")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "参加活动资格")
    public HttpCommandResultWithData allowCheck(@ApiIgnore @RequestJson ActiveForm form) {
        log.info("allowCheck start param:{}", form);
        return Result.ok(activityCheck);
    }

    /**
     * @Author liuBing
     * @Description //TODO 查看活动列表
     * @Date 17:31 2021/5/27
     * @Param [activeCommand]
     **/
    @PostMapping(value = "/checkList")
    //@LoginRequired(check = LoginCheckEnum.APP)
    public HttpCommandResultWithData checkList(@RequestJson ActivityForm form) {
        return Result.ok(activeService.checkList());
    }


    /**
     * 中秋活动，参与增加积分
     * @param form
     * @return
     */
    @PostMapping(value = "/midAutumn")
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData midAutumn(@RequestBody MidAutumnForm form){
        this.formValidate(form);
        return activeService.midAutumn(form);
    }

    @GetMapping(value = "/timeIsEnd")
    @SneakyThrows
    public HttpCommandResultWithData timeIsEnd(){
        return activeService.timeIsEnd();
    }

    /**
     * 国庆活动，参与增加积分
     * @param form
     * @return
     */
    @PostMapping(value = "/guoqing")
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData guoqing(@RequestBody GuoQingForm form){
        this.formValidate(form);
        return activeService.guoqing(form);
    }

    /**
     * 国庆活动，判断时间
     * @param
     * @return
     */
    @GetMapping(value = "/timeIsEndGuoQing")
    @SneakyThrows
    public HttpCommandResultWithData timeIsEndGuoQing(){
        return activeService.timeIsEndGuoQing();
    }

    /**
     * 双十一活动，判断时间
     * @param
     * @return
     */
    @GetMapping(value = "/timeIsEndBy11")
    @SneakyThrows
    public HttpCommandResultWithData timeIsEndBy11(){
        return activeService.timeIsEndBy11();
    }

    /**
     * 国庆活动，参与增加积分
     * @param form
     * @return
     */
    @PostMapping(value = "/shuang11")
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData shuang11(@RequestBody Shuang11Form form){
        this.formValidate(form);
        return activeService.shuang11(form);
    }


}
