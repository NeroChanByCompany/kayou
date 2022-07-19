package com.nut.locationservice.app.controller;

import com.nut.locationservice.app.dto.GetTotalMilAndOilDto;
import com.nut.locationservice.app.form.GetTotalMilAndOilForm;
import com.nut.locationservice.app.service.impl.GetTotalMilAndOilServiceImpl;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 查询位置云当日总运营里程、油耗和车辆接口
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.controller
 * @Author: yzl
 * @CreateTime: 2021-06-17 13:32
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "获取当日运营车辆总里程、油耗")
public class GetTotalMilAndOilController extends BaseController {

    @Autowired
    private GetTotalMilAndOilServiceImpl getTotalMilAndOilService;

    @PostMapping(value = "/getTotalMilAndOil")
    @ApiOperation(value = "获取总油耗、总里程")
    public HttpCommandResultWithData getTotalMilAndOil(@ApiIgnore @RequestBody GetTotalMilAndOilForm form) throws Exception {
        this.formValidate(form);
        log.info("[getTotalMilAndOil]start");
        try {
            GetTotalMilAndOilDto data = getTotalMilAndOilService.getTotalMilAndOil();
            log.info("[getTotalMilAndOil]end");
            return new HttpCommandResultWithData<>(ECode.SUCCESS.code(), ECode.SUCCESS.message(), data);
        } catch (Exception e) {
            log.error(GetTotalMilAndOilController.class.getSimpleName() + " Exception:", e);
            return new HttpCommandResultWithData(ECode.SERVER_ERROR.code(), ECode.SERVER_ERROR.message());
        }
    }

}
