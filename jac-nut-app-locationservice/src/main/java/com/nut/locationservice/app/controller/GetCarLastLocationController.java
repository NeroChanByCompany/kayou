package com.nut.locationservice.app.controller;

import com.nut.locationservice.app.dto.CarLocationToStationDto;
import com.nut.locationservice.app.form.GetCarLastLocationForm;
import com.nut.locationservice.app.service.impl.CarLastLocationServiceImpl;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
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

import java.util.Map;

/**
 * @Description: 根据通信号获取车辆末次位置信息
 * 只返回经纬度信息，不包含其他额外信息
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.controller
 * @Author: yzl
 * @CreateTime: 2021-06-17 11:05
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "获取车辆末次位置信息")
public class GetCarLastLocationController extends BaseController {

    @Autowired
    private CarLastLocationServiceImpl carLastLocationService;

    @PostMapping(value = "/carLastLocationStation")
    @ApiOperation(value = "获取车辆末次位置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terminalIdList",value = "终端号Id列表",dataType = "List<Long>" ),
    })
    public HttpCommandResultWithData handle(@ApiIgnore @RequestBody GetCarLastLocationForm form) throws Exception {
        this.formValidate(form);
        log.info("[GetCarLastLocationController] start .... ");
        try {
            Map<String, CarLocationToStationDto> map = carLastLocationService.queryCarLastLocation(form);
            log.info("[GetCarLastLocationController] end .... ");
            return new HttpCommandResultWithData(ECode.SUCCESS.code(), "", map);
        } catch (Exception e) {
            log.error(" 位置云接口异常:" + e);
            return new HttpCommandResultWithData(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());
        }
    }
}
