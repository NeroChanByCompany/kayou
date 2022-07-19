package com.nut.locationservice.app.controller;


import com.nut.locationservice.app.form.GetCarHistoryLocationForm;
import com.nut.locationservice.app.service.CarHistoryLocationService;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Author liuBing
 * @Description //TODO 车辆历史
 * @Date 10:20 2021/6/16
 **/
@Slf4j
@RestController
@Api(tags = "车辆历史位置")
@RequestMapping("/car/history/location")
public class CarHistoryLocationController extends BaseController {

    @Autowired
    public CarHistoryLocationService carHistoryLocationService;

    @PostMapping(value = "/carHistoryLocation")
    @ApiOperation(value = "车辆历史位置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terminalId",value = "终端ID",dataType = "Long"),
            @ApiImplicitParam(name = "queryDate",value = "查询时间",dataType = "Long"),
            @ApiImplicitParam(name = "index",value = "查询索引",dataType = "int"),
            @ApiImplicitParam(name = "accessTocken",value = "token",dataType = "Long"),
    })
    @SneakyThrows
    public HttpCommandResultWithData carHistoryLocation(@ApiIgnore @RequestBody GetCarHistoryLocationForm form) {
        this.formValidate(form);
        return Result.ok(carHistoryLocationService.getCarLocation(form));
    }

}
