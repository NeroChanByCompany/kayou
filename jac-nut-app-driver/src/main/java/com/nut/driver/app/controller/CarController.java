package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.dto.CarParmDto;
import com.nut.driver.app.dto.UserCarDetailDto;
import com.nut.driver.app.form.EditMaintenanceInfoForm;
import com.nut.driver.app.form.QueryMaintainItemListForm;
import com.nut.driver.app.form.QueryMaintenanceItemForm;
import com.nut.driver.app.form.UserCarsForm;
import com.nut.common.utils.JsonUtil;
import com.nut.driver.app.dao.CarDao;
import com.nut.driver.app.entity.CarEntity;
import com.nut.driver.app.form.*;
import com.nut.driver.app.service.CarExtService;
import com.nut.driver.app.service.CarService;
import com.nut.driver.app.service.CustomMaintainService;
import com.nut.driver.app.service.CouponService;
import io.swagger.annotations.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@EnableAutoConfiguration
@Api(tags = "车辆相关接口")
public class CarController extends BaseController {

    @Autowired
    private CarService carService;
    @Autowired
    private CouponService couponService;

    @Autowired
    private CarDao carDao;
    @Autowired
    private CarExtService carExtService;

    @Autowired
    private CustomMaintainService customMaintainService;

    /**
     * @return com.nut.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 用户车辆列表接口
     * @Date 16:21 2021/6/25
     * @Param [form]
     **/
    @PostMapping(value = "/user/cars")
    @LoginRequired
    @ApiOperation(value = "用户车辆列表接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "搜索关键字", dataType = "String"),
            @ApiImplicitParam(name = "returnAll", value = "是否返回全部数据", dataType = "String"),
            @ApiImplicitParam(name = "onlineStatus", value = "车辆状态", dataType = "String"),
            @ApiImplicitParam(name = "role", value = "管理角色", dataType = "String"),
            @ApiImplicitParam(name = "team", value = "车队", dataType = "String"),
            @ApiImplicitParam(name = "listOnly", value = "只返回列表数据", dataType = "String"),
            @ApiImplicitParam(name = "staticOnly", value = "只返回静态数据", dataType = "String"),
            @ApiImplicitParam(name = "exclusiveTeamId", value = "排除的车队ID", dataType = "Long"),
            @ApiImplicitParam(name = "stationId", value = "服务站ID", dataType = "String")
    })
    public HttpCommandResultWithData userCars(@ApiIgnore @RequestJson UserCarsForm form) throws Exception {
        log.info("userCars start param:{}", form);
        this.formValidate(form);
        return Result.ok(carService.query(form));
    }

    /**
     * @return com.nut.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 查询保养项目
     * @Date 16:21 2021/6/25
     * @Param [form]
     **/
    @ApiOperation(value = "查询保养项目")
    @PostMapping("/maintain/queryMaintainItemList")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(name = "maintainItemName", value = "保养项目名", dataType = "String")
    })
    @SneakyThrows
    public HttpCommandResultWithData queryMaintainItemList(@RequestJson QueryMaintainItemListForm form) {
        log.info("queryMaintainItemList start param:{}", form);
//        this.formValidate(form);
        return Result.ok(carService.queryMaintainItemList(form));
    }

    /**
     * @return com.nut.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 修改保养项目--显示保养项目
     * @Date 13:44 2021/6/27
     * @Param [form]
     **/
    @PostMapping(value = {"/maintenance/item/queryByMaintenanceId"})
    @LoginRequired
    @ApiOperation(value = "修改保养项目")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "maintainId", name = "自定义保养记录ID", dataType = "String")
    })
    public HttpCommandResultWithData queryMaintenanceItem(@RequestJson QueryMaintenanceItemForm form) throws Exception {
        log.info("queryMaintenanceItem start param:{}", form);
        this.formValidate(form);
        return Result.ok(carService.queryMaintainItemInfo(form));
    }

    /**
     * @return com.nut.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 修改信息验证
     * @Date 10:57 2021/6/28
     * @Param [form]
     **/
    @ApiOperation(value = "修改信息验证")
    @PostMapping(value = {"/maintenance/edit"})
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(name = "maintainId", value = "自定义保养记录Id", dataType = "String"),
            @ApiImplicitParam(name = "maintainName", value = "保养名称", dataType = "String"),
            @ApiImplicitParam(name = "maintainType", value = "提醒方式（1：里程；2：时间）", dataType = "String"),
            @ApiImplicitParam(name = "maintainDescribe", value = "提醒内容", dataType = "String"),
            @ApiImplicitParam(name = "acrId", value = "车辆ID", dataType = "String"),
            @ApiImplicitParam(name = "carNumber", value = "车牌号", dataType = "String"),
            @ApiImplicitParam(name = "remarks", value = "备注", dataType = "String")
    })
    public HttpCommandResultWithData editMaintenance(@RequestJson EditMaintenanceInfoForm form) throws Exception {
        log.info("editMaintenance start param:", form);
        this.formValidate(form);
        customMaintainService.editValidate(form);
        customMaintainService.editMaintainInfo(form);
        log.info("editMaintenance end return:{null}");
        return Result.ok();
    }

    /**
     * 用户删车接口
     */
    @PostMapping(value = "/user/car/delete")
    @LoginRequired
    @ApiOperation(value = "用户删车接口")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "carId", name = "车辆Id", dataType = "String")
    })
    public HttpCommandResultWithData userCarDelete(@RequestJson UserCarDeleteForm form) {
        log.info("userCarDelete start param:{}", form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = carService.delete(form);
            // 积分埋点
            /*if (result.getResultCode() == ReturnCode.OK.code()) {
                integralHandler.toIntegral(form, result);
            }*/
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("删除失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userCarDelete end return:{null}");
        //uptimeService.triggerCarBindSync(result, Collections.singletonList(form.getCarId()), UptimeVal.BIND_FLAG_UNBIND);
        return result;
    }

    /**
     * 用户加车接口（通过vin添加）
     */
    @PostMapping(value = "/user/car/addByVin")
    @ApiOperation(value = "添加车辆")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "brand", value = "品牌", dataType = "String"),
            @ApiImplicitParam(name = "carNumber", value = "车牌", dataType = "String"),
            @ApiImplicitParam(name = "vin", value = "底盘号", dataType = "String"),
            @ApiImplicitParam(name = "engineNum", value = "发动机号", dataType = "String"),
            @ApiImplicitParam(name = "carId", value = "车辆ID", dataType = "String"),
            @ApiImplicitParam(name = "license", value = "车牌号", dataType = "String"),
            @ApiImplicitParam(name = "series", value = "车系", dataType = "String"),
            @ApiImplicitParam(name = "color", value = "车身颜色", dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData userCarAddByVin(@ApiIgnore @RequestJson UserCarAddForm form) throws Exception {
        log.info("userCarAddByVin start param:{}", form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
            if (StringUtils.isBlank(form.getVin())) {
                result.setResultCode(ECode.ADD_VEHICLE_VIN_EMPTY.code());
                result.setMessage("底盘号不能为空");
            } else {
               result = carService.addByChassisNumber(form);
                if (result.getResultCode() == ECode.SUCCESS.code()) {
                    saveCarExt(form);
                    int bind = couponService.carIsBind(form.getVin());
                    if (bind == 0) {
                        log.info("[----用户加车，添加优惠券----]Start");
                        Map<String, Object> params = new HashMap<>();
                        params.put("vin", form.getVin());
                        params.put("createTime", new Date());
                        couponService.insertIntoCouponCarBind(params);
                        ScoreBindcarReceiveForm command1 = new ScoreBindcarReceiveForm();
                        command1.setUserId(form.getUserId());
                        command1.setVin(form.getVin());
                        result = couponService.scoreBindcarReceive(command1);
                        if (result.getResultCode() != ECode.SUCCESS.code()) {
                            result.setResultCode(ECode.CLIENT_ERROR.code());
                            log.info("[----用户加车，添加优惠券----]ErrResult:" + JsonUtil.toJson(result));
                        }
                        log.info("[----用户加车，添加优惠券----]Success");
                        log.info("[----用户加车，添加优惠券----]End");
                    } else {
                        log.info("[----用户加车，vin={}已经添加了优惠券----]", form.getVin());
                    }
                }
            }
        log.info("userCarAddByVin end return:{null}");
        return result;
    }

    private void saveCarExt(UserCarAddForm command) throws Exception {
        UpdateCarExtForm updateCarExtCommand = new UpdateCarExtForm();
        CarEntity car = carDao.selectByChassisNumber(command.getVin());
        updateCarExtCommand.setCarId(Long.parseLong(car.getId()));
        updateCarExtCommand.setVin(car.getCarVin());
        updateCarExtCommand.setLicense(command.getLicense());
        updateCarExtCommand.setBrand(command.getBrand());
        updateCarExtCommand.setSeries(command.getSeries());
        updateCarExtCommand.setEngineNum(command.getEngineNum());
        updateCarExtCommand.setColor(command.getColor());
        updateCarExtCommand.setUcId(command.getUserId());
        updateCarExtCommand.setUserId(command.getUserId());
        carExtService.save(updateCarExtCommand);
    }

    /**
     * @Description：${用户车辆详情接口}
     * @author YZL
     * @data 2021/6/29 17:18
     */
    @PostMapping(value = "/user/carDetail")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "用户车辆详情")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "carId", name = "车辆Id", dataType = "String"),
            @ApiImplicitParam(value = "realtimeFlag", name = "只获取实时信息标识（1：是，当设置此值时，返回字段只有实时信息，其他字段均返回null）", dataType = "String")
    })
    public HttpCommandResultWithData userCarDetail(@ApiIgnore @RequestJson UserCarDetailForm form) {
        log.info("userCarDetail start param:{}", form);
        this.formValidate(form);
        UserCarDetailDto userCarDetailDto = null;
        try {
            userCarDetailDto = carService.detail(form);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "查询失败，请稍后重试");
        }
        log.info("userCarDetail end return:{}", userCarDetailDto);
        return Result.ok(userCarDetailDto);
    }

    /**
     * @Description：查询车辆参数
     * @author YZL
     * @data 2021/6/29 10:03
     */
    @PostMapping(value = "/user/car/info")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "查询车辆参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chassisNUm", value = "车辆vin", dataType = "String")
    })
    public HttpCommandResultWithData userCarInfo(@ApiIgnore @RequestJson UserCarInfoForm form) {
        log.info("userCarInfo start param:{}", form);
        this.formValidate(form);
        return Result.ok(carService.findByChassisNumber(form.getChassisNum()));
    }


    /**
     * @Description：${车辆参数}
     * @author YZL
     * @data 2021/6/29 17:42
     */
    @PostMapping(value = "/carParm")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "车辆参数")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "carId", name = "车辆Id", dataType = "String")
    })
    public HttpCommandResultWithData queryCarParm(@ApiIgnore @RequestJson QueryCarParmForm form) {
        log.info("queryCarParm start param:{}", form);
        this.formValidate(form);
        CarParmDto dto = null;
        try {
            dto = carService.getCarParmInfo(form);
        } catch (Exception e) {
            log.error(" QueryCarParmController Exception:", e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "车辆信息查询失败!");
        }
        log.info("queryCarParm end return:{}", dto);
        return Result.ok(dto);

    }

    @LoginRequired
    @PostMapping("/getCarBasicInfo")
    @SneakyThrows
    @ApiOperation(value = "获取车辆基本信息 sim卡号和iccid")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "carId", name = "车辆Id", dataType = "String")
    })
    public HttpCommandResultWithData getCarBasicInfo(@RequestBody CarBasicInfoForm form){
        log.info("getCarBasicInfo 对接tsp获取车辆基础信息 start param：{}",form);
        this.formValidate(form);
        return Result.ok(carService.getCarBasicInfo(form));
    }
}
