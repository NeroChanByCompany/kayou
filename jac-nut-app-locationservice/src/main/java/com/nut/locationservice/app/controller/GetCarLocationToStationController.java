package com.nut.locationservice.app.controller;

import com.nut.locationservice.app.dto.CarLocationToStationDto;
import com.nut.locationservice.app.form.GetCarLocationToStationForm;
import com.nut.locationservice.app.service.impl.CarLocationToStationServiceImpl;
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

import java.util.List;

/**
 * @Description: 根据通信号获取车辆末次位置信息
 *               只供服务站附近车辆查询使用
 *               其他额外信息返回
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.controller
 * @Author: yzl
 * @CreateTime: 2021-06-17 11:15
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "服务站附近车辆")
public class GetCarLocationToStationController extends BaseController {

    @Autowired
    public CarLocationToStationServiceImpl carLocationToStationService;

    @PostMapping(value = "/carLocationStation")
    @ApiOperation(value = "服务站附近车辆")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terminalIdList",value = "终端ID列表",dataType = "List"),
            @ApiImplicitParam(name = "lon",value = "服务站经度",dataType = "Double"),
            @ApiImplicitParam(name = "lat",value = "服务站纬度",dataType = "Double"),
            @ApiImplicitParam(name = "range",value = "查询范围半径",dataType = "int")
    })
    public HttpCommandResultWithData handle(@ApiIgnore @RequestBody GetCarLocationToStationForm form) throws Exception {
        this.formValidate(form);
        log.info("[GetCarLocationToStationController] start .... ");
        try {
            List<CarLocationToStationDto> list = carLocationToStationService.queryCarLocation(form);
            log.info("[GetCarLocationToStationController] end .... ");
            return new HttpCommandResultWithData(ECode.SUCCESS.code(), "", list);
        } catch (Exception e) {
            log.error(" 位置云接口异常:" + e);
            return new HttpCommandResultWithData(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());
        }
    }

}
