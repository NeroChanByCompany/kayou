package com.nut.driver.app.controller;

import com.nut.common.annotation.RequestJson;
import com.nut.driver.app.form.*;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.service.CustomMaintainService;
import com.nut.driver.app.service.ItemsListService;
import com.nut.driver.app.service.MaintainNewService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 项目列表：维修项目与保养项目
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.controller
 * @Author: yzl
 * @CreateTime: 2021-06-23 13:39
 * @Version: 1.0
 */
@Api(tags = "项目列表：维修项目与保养项目")
@RestController
@Slf4j
public class ItemsListController extends BaseController {

    @Autowired
    private ItemsListService itemsListService;

    @Autowired
    private MaintainNewService maintainNewService;

    @Autowired
    private CustomMaintainService customMaintainService;

    @PostMapping(value = "/orderItems")
    @SneakyThrows
    @LoginRequired
    @ApiOperation(value = "维修保养项目列表")
    public HttpCommandResultWithData orderItems(@ApiIgnore @RequestJson QueryAllAppointmentItemListForm form) {
        log.info("orderItems start param:{}",form);
        return Result.ok(itemsListService.orderItems(form));
    }

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing/queryCarNewMaintainDetails
     * @Description //TODO 推荐保养车辆列表
     * @Date 17:05 2021/6/23
     * @Param [command]
     **/
    @Deprecated
    @ApiOperation(value = "推荐保养车辆列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "底盘号", dataType = "String"),
    })
    @PostMapping(value = "/queryCarNewMaintainList")
    @LoginRequired
    public HttpCommandResultWithData queryCarMaintainList(@ApiIgnore @RequestJson QueryCarMaintainListForm form) {
        log.info("queryCarMaintainList start param:{}",form);
        return Result.ok(maintainNewService.queryCarMaintainPage(form));
    }


    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 推荐保养车辆列表
     * @Date 17:05 2021/6/23
     * @Param [command]
     **/
    @ApiOperation(value = "推荐保养车辆列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "底盘号", dataType = "String"),
    })
    @PostMapping(value = "/getCarNewMaintainList")
    @LoginRequired
    public HttpCommandResultWithData getCarNewMaintainList(@ApiIgnore @RequestJson QueryCarMaintainListForm form) {
        log.info("queryCarMaintainList start param:{}",form);
        return Result.ok(maintainNewService.getCarMaintainPage(form));
    }

    /**
     * @return com.nut.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 推荐保养车辆详情
     * @Date 13:22 2021/6/24
     * @Param [form]
     **/
    @ApiOperation(value = "推荐保养车辆详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "carVin", value = "底盘号", dataType = "String"),
            @ApiImplicitParam(name = "classifyCode", value = "保养项目类编码", dataType = "String"),
    })
    @PostMapping(value = "/queryCarNewMaintainDetails")
    @LoginRequired
    @Deprecated
    public HttpCommandResultWithData queryCarNewMaintainDetails(@ApiIgnore @RequestJson QueryCarNewMaintainDetailsForm form) throws Exception {
        log.info("queryCarNewMaintainDetails start pram:{}",form);
        this.formValidate(form);
        return Result.ok(maintainNewService.queryCarNewMaintainDetails(form));
    }


    /**
     * @return com.nut.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 推荐保养车辆详情
     * @Date 13:22 2021/6/24
     * @Param [form]
     **/
    @ApiOperation(value = "推荐保养车辆详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "carVin", value = "底盘号", dataType = "String"),
            @ApiImplicitParam(name = "classifyCode", value = "保养项目类编码", dataType = "String"),
    })
    @PostMapping(value = "/getCarNewMaintainDetails")
    @LoginRequired
    public HttpCommandResultWithData getCarNewMaintainDetails(@ApiIgnore @RequestJson QueryCarNewMaintainDetailsForm form) throws Exception {
        log.info("queryCarNewMaintainDetails start pram:{}",form);
        this.formValidate(form);
        return Result.ok(maintainNewService.getCarNewMaintainDetails(form));
    }
    /**
     * @return com.nut.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 查询保养记录
     * @Date 13:22 2021/6/24
     * @Param [command]
     **/
    @ApiOperation(value = "查询保养记录")
    @PostMapping(value = "/queryNewMaintainRecord")
    @LoginRequired
    @ApiImplicitParams({
            @ApiImplicitParam(name = "carVin",value = "车辆底盘号",dataType = "String"),
            @ApiImplicitParam(name = "calssifyCode",value = "保养项目类编码",dataType = "String")
    })
    public HttpCommandResultWithData queryNewMaintainRecord(@RequestJson QueryCarNewMaintainDetailsForm form) throws Exception {
        log.info("queryNewMaintainRecord start param:{}",form);
        this.formValidate(form);
        return Result.ok(maintainNewService.queryNewMaintainRecord(form));
    }

    /**
     * @return com.nut.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 更新车辆保养信息
     * @Date 13:52 2021/6/24
     * @Param [command]
     **/
    @LoginRequired
    @ApiOperation(value = "更新车辆保养信息")
    @PostMapping(value = "/updateCarNewMaintain")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "carMaintainIdList",value = "车辆保养Id",dataType = "String"),
            @ApiImplicitParam(name = "maintainMileage",value = "保养里程",dataType = "String"),
            @ApiImplicitParam(name = "maintainTime",value = "保养时间",dataType = "String"),
            @ApiImplicitParam(name = "maintainSource",value = "保养更新来源：客户APP：1；服务APP：2；crm同步：3",dataType = "Integer"),
    })
    public HttpCommandResultWithData updateCarNewMaintain(@RequestJson UpdateCarNewMaintainFrom form) throws Exception {
        log.info("updateCarNewMaintain start param:{}",form);
        this.formValidate(form);
        maintainNewService.updateCarNewMaintain(form);
        return Result.ok();
    }


    /**
     * @return com.nut.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 查询自定义保养列表
     * @Date 14:29 2021/6/24
     * @Param [command]
     **/
    @RequestMapping(value = { "/driver/maintenance/list"},  method = RequestMethod.POST)
    @LoginRequired
    @ApiOperation(value = "查询自定义保养列表")
    public HttpCommandResultWithData queryDriverMaintenanceList(@ApiIgnore @RequestJson QueryDriverMaintenanceListForm form) {
        log.info("queryDriverMaintenanceList start param:{}",form);
        return Result.ok(customMaintainService.queryDriverMaintenanceList(form));
    }

    /**
     * @return com.nut.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 自定义保养列表详情
     * @Date 15:11 2021/6/24
     * @Param [command]
     **/
    @PostMapping(value = { "/maintenance/info"})
    @LoginRequired
    @ApiOperation(value = "自定义保养列表详情")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "maintainId",value = "自定义保养记录id",dataType = "String")
    })
    public HttpCommandResultWithData queryMaintenanceInfo(@RequestJson QueryMaintenanceInfoForm form) throws Exception {
        log.info("queryMaintenanceInfo start param:{}",form);
        this.formValidate(form);
        return Result.ok(customMaintainService.queryMaintanceInfo(form));
    }
    /**
     * @Author liuBing
     * @Description //TODO 删除车辆保养信息
     * @Date 16:18 2021/6/24
     * @Param [command]
     * @return com.nut.common.result.HttpCommandResultWithData
     **/
    @PostMapping(value = {"maintenance/delete"})
    @LoginRequired
    @ApiOperation(value = "删除车辆保养信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "maintainId",value = "自定义保养记录id",dataType = "String")
    })
    public HttpCommandResultWithData deleteMaintenance(@RequestJson QueryMaintenanceInfoForm form) throws Exception {
        log.info("deleteMaintenance start param:{}",form);
        this.formValidate(form);
        customMaintainService.deleteMaintainceInfo(form);
        return Result.ok();
    }
    /**
     * @Author liuBing
     * @Description //TODO 添加车辆保养信息
     * @Date 16:39 2021/6/24
     * @Param [command]
     * @return com.nut.common.result.HttpCommandResultWithData
     **/
    @PostMapping(value = { "/maintenance/add"})
    @LoginRequired
    @ApiOperation(value = "添加车辆保养信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId",value = "车队Id",dataType = "String"),
            @ApiImplicitParam(name = "maintainName",value = "保养名称",dataType = "String"),
            @ApiImplicitParam(name = "maintainType",value = "提醒方式 1：里程；2：时间",dataType = "String"),
            @ApiImplicitParam(name = "maintainDescribe",value = "提醒内容",dataType = "String"),
            @ApiImplicitParam(name = "carId",value = "车辆id",dataType = "String"),
            @ApiImplicitParam(name = "carNumber",value = "车牌号",dataType = "String"),
            @ApiImplicitParam(name = "remarks",value = "备注",dataType = "String"),
            @ApiImplicitParam(name = "maintainItemIdList",value = "保养项目id列表 id用“,”分割",dataType = "String")
    })
    public HttpCommandResultWithData addMaintenance(@RequestJson AddMaintenanceInfoForm form) throws Exception {
        log.info("addMaintenance start param:{}",form);
        this.formValidate(form);
        customMaintainService.addValidate(form);
        return Result.ok();
    }

}
