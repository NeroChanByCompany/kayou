package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.app.form.*;
import com.nut.driver.app.service.AdminService;
import com.nut.driver.app.service.FltFleetUserMappingService;
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
 * @Description: 管理员相关
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-28 10:22
 * @Version: 1.0
 */
@RestController
@Api(tags = "管理员操作相关")
@Slf4j
public class AdminController extends BaseController {

    @Autowired
    private FltFleetUserMappingService fltFleetUserMappingService;

    @Autowired
    private AdminService adminService;

    @PostMapping(value = "/fleet/admin/bind")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "车队与管理员绑定")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId", value = "车队ID", dataType = "Long"),
            @ApiImplicitParam(name = "adminId", value = "管理员用户ID", dataType = "String")
    })
    public HttpCommandResultWithData fleetAdminBind(@ApiIgnore @RequestJson FleetAdminBindForm form) {
        log.info("fleetAdminBind start param:{}", form);
        this.formValidate(form);
        return Result.ok(fltFleetUserMappingService.bind(form));
    }

    /**
     * @Description：解除管理员和车队的绑定
     * @author YZL
     * @data 2021/6/29 13:37
     */
    @PostMapping(value = "/fleet/admin/unbind")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "解除管理员和车队的绑定")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId", value = "车队ID", dataType = "Long"),
            @ApiImplicitParam(name = "adminId", value = "管理员用户ID", dataType = "String")
    })
    public HttpCommandResultWithData fleetAdminUnbind(@ApiIgnore @RequestJson FleetAdminUnbindForm form) {
        log.info("fleetAdminUnbind start param:{}", form);
        this.formValidate(form);
        return Result.ok(fltFleetUserMappingService.unbind(form));
    }

    /**
     * 管理员退出车队接口
     */
    @PostMapping(value = "/fleet/admin/quit")
    @ApiOperation("管理员退出车队")
    @ApiImplicitParam(name = "teamId", value = "车队ID", dataType = "Long")
    @LoginRequired
    public HttpCommandResultWithData fleetAdminQuit(@RequestJson FleetAdminQuitForm form) {
        log.info("fleetAdminQuit start param:{}", form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = adminService.quit(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("提交失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("[fleetAdminQuit]end");
        return result;
    }
    /**
     *  @author wuhaotian 2021/6/28
     */
    /**
     * 车队管理员列表接口
     */
    @PostMapping(value = "/fleet/admins")
    @ApiOperation("车队管理员列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teamId", value = "车队ID", dataType = "Long"),
            @ApiImplicitParam(name = "keyword", value = "关键字", dataType = "String"),
            @ApiImplicitParam(name = "returnAll", value = "是否返回全部数据", dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData fleetAdmins(@RequestJson FleetAdminsForm form) {
        log.info("fleetAdmins start param:{}", form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = adminService.query(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("[fleetAdmins]end");
        return result;
    }


}
