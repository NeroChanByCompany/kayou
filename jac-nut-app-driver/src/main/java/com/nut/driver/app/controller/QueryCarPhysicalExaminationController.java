package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.dto.CarPhyExaDetailDto;
import com.nut.driver.app.dto.CarPhyExaDto;
import com.nut.driver.app.form.QueryCarPhysicalExaminationDetailForm;
import com.nut.driver.app.form.QueryCarPhysicalExaminationForm;
import com.nut.driver.app.service.CarPhysicalExaminationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 车辆体检
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-29 15:32
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "车辆体检")
public class QueryCarPhysicalExaminationController extends BaseController {

    @Autowired
    private CarPhysicalExaminationService carPhysicalExaminationService;

    /**
     * @Description：车辆体检
     * @author YZL
     * @data 2021/6/29 15:58
     */
    @PostMapping(value = "/carPhysicalExamination")
    @ApiOperation("诊断列表")
    @ApiImplicitParam(name = "carNumber", value = "车牌号", dataType = "String")
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData carPhysicalExamination(@ApiIgnore @RequestJson QueryCarPhysicalExaminationForm form) {
        log.info("carPhysicalExamination start param:{}", form);
        CarPhyExaDto carPhyExaDto = null;
        try {
            carPhyExaDto = carPhysicalExaminationService.carPhysicalExamination(form);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result.result(ECode.SERVER_ERROR.code(), "车辆体检查询失败");
        }
        log.info("carPhysicalExamination end return:{}", carPhyExaDto);
        return Result.ok(carPhyExaDto);
    }

    /**
     * @Description：车辆体检详情
     * @author YZL
     * @data 2021/6/29 15:59
     */
    @PostMapping(value = "/carPhysicalExaminationDetail")
    @ApiOperation("诊断详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "carId", value = "车辆ID", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "查询类型", dataType = "Integer")
    })
    @SneakyThrows
    public HttpCommandResultWithData carPhysicalExaminationDetail(@ApiIgnore @RequestJson QueryCarPhysicalExaminationDetailForm form) {
        log.info("carPhysicalExaminationDetail start param:{}", form);
        this.formValidate(form);
        CarPhyExaDetailDto carPhyExaDetailDto = new CarPhyExaDetailDto();
        try {
            carPhyExaDetailDto = carPhysicalExaminationService.carPhysicalExaminationDetail(form);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            Result.result(ECode.SERVER_ERROR.code(), "车辆体检查询失败");
        }
        log.info("carPhysicalExaminationDetail end return:{}", carPhyExaDetailDto);
        return Result.ok(carPhyExaDetailDto);
    }

}
