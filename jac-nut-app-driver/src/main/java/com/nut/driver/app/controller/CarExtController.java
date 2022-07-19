package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.entity.CarExtEntity;
import com.nut.driver.app.entity.DictVehicleUseEntity;
import com.nut.driver.app.form.QueryCarExtForm;
import com.nut.driver.app.form.QueryDictVehicleUseForm;
import com.nut.driver.app.form.UpdateCarExtForm;
import com.nut.driver.app.service.CarExtService;
import com.nut.driver.app.service.DictVehicleUseService;
import com.vdurmont.emoji.EmojiManager;
import com.vdurmont.emoji.EmojiParser;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

/**
 * @Description: 车辆控制器
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-29 09:23
 * @Version: 1.0
 */
@RestController
@Slf4j
public class CarExtController extends BaseController {

    @Autowired
    private DictVehicleUseService dictVehicleUseService;

    @Autowired
    private CarExtService carExtService;

    /**
     * @Description：用户车辆用途
     * @author YZL
     * @data 2021/6/29 9:23
     */
    @PostMapping(value = "/car/ext/dictVehicleUse")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "用户车辆用途")
    public HttpCommandResultWithData findAll(@ApiIgnore @RequestJson QueryDictVehicleUseForm form) {
        log.info("findAll start param:{}", form);
        List<DictVehicleUseEntity> dictVehicleUseEntityList = null;
        try {
            dictVehicleUseEntityList = dictVehicleUseService.findAll();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "查询失败，请稍后重试");
        }
        log.info("findAll end return:{}", dictVehicleUseEntityList);
        return Result.ok(dictVehicleUseEntityList);
    }

    /**
     * @Description：车辆参数保存
     * @author YZL
     * @data 2021/6/29 9:49
     */
    @PostMapping(value = "/car/ext/save")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "车辆参数保存")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "carId", value = "车辆表id", dataType = "Long"),
            @ApiImplicitParam(name = "vin", value = "车辆vin", dataType = "String"),
            @ApiImplicitParam(name = "license", value = "车牌号", dataType = "String"),
            @ApiImplicitParam(name = "identity", value = "车辆身份", dataType = "String"),
            @ApiImplicitParam(name = "brand", value = "品牌", dataType = "String"),
            @ApiImplicitParam(name = "series", value = "车系", dataType = "String"),
            @ApiImplicitParam(name = "engineNum", value = "发动机号", dataType = "String"),
            @ApiImplicitParam(name = "color", value = "车辆颜色", dataType = "String"),
            @ApiImplicitParam(name = "vehicleUse", value = "车辆用途", dataType = "Integer"),
            @ApiImplicitParam(name = "industry", value = "行业", dataType = "String"),
            @ApiImplicitParam(name = "typeOfGoods", value = "货物类型", dataType = "String"),
            @ApiImplicitParam(name = "typeOfVan", value = "货车类型", dataType = "String"),
            @ApiImplicitParam(name = "ratedLoad", value = "核定载重", dataType = "String"),
            @ApiImplicitParam(name = "weight", value = "货车重量", dataType = "String"),
            @ApiImplicitParam(name = "length", value = "货车长度", dataType = "String"),
            @ApiImplicitParam(name = "width", value = "货车宽度", dataType = "String"),
            @ApiImplicitParam(name = "insuranceDate", value = "保险到期时间", dataType = "String"),
            @ApiImplicitParam(name = "insuredAmount", value = "保险金额", dataType = "String"),
            @ApiImplicitParam(name = "insuranceCompany", value = "保险公司", dataType = "String"),
            @ApiImplicitParam(name = "transferCycle", value = "换车周期", dataType = "String"),
    })
    public HttpCommandResultWithData findByCarId(@ApiIgnore @RequestJson  UpdateCarExtForm form) {
        log.info("findByCarId start param:{}", form);
        List<String> strings = EmojiParser.extractEmojis(form.getSeries());
        if (CollectionUtils.isNotEmpty(strings)){
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "请输入正确的车系名称！");
        }
        try {
            carExtService.save(form);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "查询失败，请稍后重试");
        }
        log.info("findByCarId end return:{null}");
        return Result.ok();
    }

    /**
     * @Description：查询车辆拓展参数
     * @author YZL
     * @data 2021/6/29 9:57
     */
    @PostMapping(value = "/car/ext")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "查询车辆拓展参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "carId", value = "车辆Id", dataType = "Long")
    })
    public HttpCommandResultWithData findByCarId(@ApiIgnore @RequestJson  QueryCarExtForm form) {
        log.info("查询车辆拓展参数findByCarId start param:{}", form);
        this.formValidate(form);
        CarExtEntity carExtEntity = null;
        try {
            carExtEntity = carExtService.findById(form.getCarId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "查询失败，请稍后重试");
        }
        log.info("查询车辆拓展参数findByCarId end return:{}", carExtEntity);
        return Result.ok(carExtEntity);
    }

}
