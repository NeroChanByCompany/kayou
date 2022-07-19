package com.nut.locationservice.app.controller;

import com.nut.locationservice.app.form.GenerateCarInfoFileForm;
import com.nut.locationservice.app.service.ExportCarInfoFileService;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.utils.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;


/**
 * @Description: 生成车辆故障信息文件
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.controller
 * @Author: yzl
 * @CreateTime: 2021-06-16 20:27
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "生成车辆故障信息文件")
public class ExportCarInfoFileController extends BaseController {

    @Autowired
    private ExportCarInfoFileService exportCarInfoFileService;

    @Value("${internalApiUser:aerozhApiUser}")
    private String usr;

    @Value("${internalApiPwd:f0a7ffd87ae44b7a}")
    private String pwd;

    @PostMapping(value = {"/generateCarFaultInfoFile"})
    @ApiOperation(value = "生成车辆失败信息文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "procDate",value = "处理日期",dataType = "String"),
            @ApiImplicitParam(name = "usr",value = "用户Id",dataType = "String"),
            @ApiImplicitParam(name = "pwd",value = "密码",dataType = "String"),
    })
    @SneakyThrows
    public HttpCommandResultWithData generateCarFaultInfoFile(@ApiIgnore @RequestBody GenerateCarInfoFileForm form) {
        this.formValidate(form);
        log.info("[ExportCarInfoFileController]generateCarFaultInfoFile start");
        long startMillis = System.currentTimeMillis();
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        //校验
        if (StringUtil.isNotEq(usr, form.getUsr()) || StringUtil.isNotEq(pwd, form.getPwd())) {
            result.setResultCode(ECode.USER_DNE.code());
            result.setMessage("接口调用鉴权错误！");
            return result;
        }

        try {
            result = exportCarInfoFileService.generateCarFaultInfoFile(form.getProcDate());
            log.info("[生成车辆故障信息文件]成功，总耗时：{}ms", System.currentTimeMillis() - startMillis);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("生成车辆故障信息文件失败");
            log.error(e.getMessage(), e);
        }
        log.info("[ExportCarInfoFileController]generateCarFaultInfoFile end");
        return result;
    }


    @PostMapping(value = {"/generateCarTrackInfoFile"})
    @ApiOperation(value = "生成车辆轨迹信息文件")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "procDate",value = "处理日期",dataType = "String"),
            @ApiImplicitParam(name = "usr",value = "用户Id",dataType = "String"),
            @ApiImplicitParam(name = "pwd",value = "密码",dataType = "String"),
    })
    @SneakyThrows
    public HttpCommandResultWithData generateCarTrackInfoFile(@ApiIgnore @RequestBody GenerateCarInfoFileForm form) {
        this.formValidate(form);
        log.info("[ExportCarInfoFileController]generateCarTrackInfoFile start");
        long startMillis = System.currentTimeMillis();
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        //校验
        if (StringUtil.isNotEq(usr, form.getUsr()) || StringUtil.isNotEq(pwd, form.getPwd())) {
            result.setResultCode(ECode.USER_DNE.code());
            result.setMessage("接口调用鉴权错误！");
            return result;
        }
        try {
            result = exportCarInfoFileService.generateCarTrackInfoFile(form.getProcDate());
            log.info("[生成车辆轨迹信息文件]成功，总耗时：{}ms", System.currentTimeMillis() - startMillis);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("生成车辆轨迹信息文件失败");
            log.error(e.getMessage(), e);
        }
        log.info("[ExportCarInfoFileController]generateCarTrackInfoFile end");
        return result;
    }

}
