package com.nut.locationservice.app.controller;

import com.nut.locationservice.app.dto.LastLocationDto;
import com.nut.locationservice.app.form.GetCarLastLocationByAutForm;
import com.nut.locationservice.app.service.impl.LastLocationServiceImpl;
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
 * @Description: 针对行程整合redis拆分，末次位置信息取位置云
 *               不再取行程整合保存的末次位置
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.controller
 * @Author: yzl
 * @CreateTime: 2021-06-17 10:53
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "自动化获取车辆末次位置信息")
public class GetCarLastLocationByAutController extends BaseController {

    @Autowired
    private LastLocationServiceImpl lastLocationService;

    @PostMapping(value = "/getCarLastLocationByAut")
    @ApiOperation(value = "获取车辆末次位置信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "terminalIdList",value = "终端号Id列表",dataType = "List" ),
    })
    public HttpCommandResultWithData handle(@ApiIgnore @RequestBody GetCarLastLocationByAutForm form) throws Exception {
        this.formValidate(form);
        log.info(" =====[GetCarLastLocationByAutController]  start==========");
        log.info("param:{}", form.toString());
        try {
            Map<Long, LastLocationDto> map = lastLocationService.queryCarLastLocationByAutoTerminal(form);
            log.info(" ===== [GetCarLastLocationByAutController] end==========");
            return new HttpCommandResultWithData(ECode.SUCCESS.code(), "", map);
        } catch (Exception e) {
            log.error(" 位置云接口异常:" + e);
            return new HttpCommandResultWithData(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());
        }
    }

}
