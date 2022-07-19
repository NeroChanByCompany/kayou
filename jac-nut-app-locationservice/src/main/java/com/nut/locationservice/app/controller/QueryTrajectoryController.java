package com.nut.locationservice.app.controller;

import com.nut.locationservice.app.form.QueryTrajectoryForm;
import com.nut.locationservice.app.service.impl.QueryTrajectoryServiceImpl;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 查询位置云 车辆轨迹
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.controller
 * @Author: yzl
 * @CreateTime: 2021-06-17 13:41
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "查询车辆轨迹")
public class QueryTrajectoryController extends BaseController {

    @Autowired
    private QueryTrajectoryServiceImpl queryTrajectoryService;

    @PostMapping(value = "/queryTrajectory")
    @ApiOperation(value = "查询车辆轨迹")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terminalId",value = "终端Id",dataType = "Long"),
            @ApiImplicitParam(name = "startTime",value = "开始时间",dataType = "Long"),
            @ApiImplicitParam(name = "endTime",value = "结束时间",dataType = "Long"),
    })
    public HttpCommandResultWithData handle(@ApiIgnore @RequestBody QueryTrajectoryForm form) throws Exception {
        this.formValidate(form);
        log.info("[QueryTrajectoryController] start ....... ");
        HttpCommandResultWithData result1 = new HttpCommandResultWithData();
        try {
            result1 = queryTrajectoryService.queryTrajectory(form);
            log.info("[QueryTrajectoryController1] end ....... ");
            return result1;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("[QueryTrajectoryController2] end ....... ");
        return result1;
    }

}
