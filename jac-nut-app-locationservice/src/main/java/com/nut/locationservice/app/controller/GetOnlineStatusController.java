package com.nut.locationservice.app.controller;
import com.nut.locationservice.app.dto.CarOnlineStatusDTO;
import com.nut.locationservice.app.form.GetOnlineStatusForm;
import com.nut.locationservice.app.service.impl.CarOnlineStatusServiceImpl;
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
 * @Description: 查询位置云车辆状态
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.controller
 * @Author: yzl
 * @CreateTime: 2021-06-17 11:24
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "获取在线车辆状态")
public class GetOnlineStatusController extends BaseController {

    @Autowired
    private CarOnlineStatusServiceImpl carOnlineStatusService;

    @PostMapping(value = "/getOnlineStatus")
    @ApiOperation(value = "获取在线车辆状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tidList",value = "通讯ID列表",dataType = "List<Long>"),
            @ApiImplicitParam(name = "vinList",value = "底盘号列表",dataType = "List<String>"),
            @ApiImplicitParam(name = "sourceFlag",value = "查询方式",dataType = "Boolean"),
            @ApiImplicitParam(name = "flagStatus",value = "查询方式",dataType = "Boolean")
    })
    public HttpCommandResultWithData handle(@ApiIgnore @RequestBody GetOnlineStatusForm form) throws Exception {
        this.formValidate(form);
        log.info("[GetOnlineStatusController] start ....... ");
        try {
            Map<String, CarOnlineStatusDTO> resultMap = carOnlineStatusService.getOnlineStatus(form);
            log.info("[GetOnlineStatusController] end ....... ");
            return new HttpCommandResultWithData(ECode.SUCCESS.code(), ECode.SUCCESS.message(), resultMap);
        } catch (Exception e) {
            log.error(GetOnlineStatusController.class.getSimpleName() + " Exception:", e);
            return new HttpCommandResultWithData(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());
        }
    }

}
