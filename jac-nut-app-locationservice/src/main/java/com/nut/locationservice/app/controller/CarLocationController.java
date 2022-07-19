package com.nut.locationservice.app.controller;

import com.nut.common.result.Result;
import com.nut.locationservice.app.dto.CarLocationOutputDto;
import com.nut.locationservice.app.form.GetCarLocationForm;
import com.nut.locationservice.app.service.CarPosService;
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
import java.util.Map;

/**
 * @Description: // TODO: 2021/6/16 车辆位置
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.controller
 * @Author: yzl
 * @CreateTime: 2021-06-16 17:45
 * @Version: 1.0
 */
@RestController
@Api(tags = "车辆位置")
@Slf4j
public class CarLocationController extends BaseController {

    @Autowired
    private CarPosService carPosService;

    @PostMapping(value = "/carLocation")
    @ApiOperation(value = "车辆位置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "vins",value = "车辆底盘号",dataType = "String"),
            @ApiImplicitParam(name = "carSource",value = "业务来源",dataType = "String"),
    })
    public HttpCommandResultWithData handle(@ApiIgnore @RequestBody GetCarLocationForm form) throws Exception {
        this.formValidate(form);
        log.info(" =====controller  start==========");
        try {
            Map<String, CarLocationOutputDto> locateCarMap = carPosService.getLocationByVins(form);
            log.info(" =====controller end==========");
            return new HttpCommandResultWithData(ECode.SUCCESS.code(), "", locateCarMap);
        } catch (Exception e) {
            log.error(" 位置云接口异常:" + e);
            return new HttpCommandResultWithData(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());
        }
    }

    /**
     * @Author liuBing
     * @Description //TODO 车辆最后位置
     * @Date 11:08 2021/6/24
     * @Param [form]
     * @return com.nut.common.result.HttpCommandResultWithData
     **/
    @PostMapping(value = "/lastLocation")
    @ApiOperation(value = "车辆位置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "commIds",value = "id",dataType = "list"),
    })
    public HttpCommandResultWithData lastLocation(@ApiIgnore @RequestBody List<Long> commIds) throws Exception {
        return Result.ok(carPosService.queryJsonLastLocation(commIds));
    }



}
