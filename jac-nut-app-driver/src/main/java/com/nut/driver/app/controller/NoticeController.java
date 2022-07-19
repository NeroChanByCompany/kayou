package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.PagingInfo;
import com.nut.common.result.Result;
import com.nut.driver.app.dto.QueryAlarmNoticeDetailListDto;
import com.nut.driver.app.dto.QueryAlarmNoticeListAttributeDto;
import com.nut.driver.app.form.*;
import com.nut.driver.app.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 报警通知
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.controller
 * @Author: yzl
 * @CreateTime: 2021-06-30 10:09
 * @Version: 1.0
 */
@RestController
@Slf4j
@Api(tags = "报警通知")
public class NoticeController extends BaseController {

    @Autowired
    private NoticeService noticeService;

    /**
     * @Description：报警通知列表
     * @author YZL
     * @data 2021/6/30 10:51
     */
    @PostMapping(value = "/queryAlarmNoticeList")
    @ApiOperation("报警通知列表")
    @CrossOrigin
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData queryAlarmNoticeList(@ApiIgnore @RequestJson QueryNoticeListForm form) {
        log.info("queryAlarmNoticeList start param:{}",form);
        QueryAlarmNoticeListAttributeDto queryAlarmNoticeListAttributeDto = null;
        try {
            queryAlarmNoticeListAttributeDto = noticeService.queryAlarmNoticeList(form);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ExceptionUtil.result(ECode.SERVER_ERROR.code(), "查询报警通知列表失败");
        }
        log.info("queryAlarmNoticeList end return:{}",queryAlarmNoticeListAttributeDto);
        return Result.ok(queryAlarmNoticeListAttributeDto);
    }

    /**
     * @Description：查询报警通知详情列表
     * @author YZL
     * @data 2021/6/30 10:53
     */
    @PostMapping(value = "/queryAlarmNoticeDetailList")
    @ApiOperation("报警通知车辆列表")
    @ApiImplicitParam(name = "carId" , value = "车辆ID" , dataType = "String")
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData queryAlarmNoticeDetailList(@ApiIgnore @RequestJson QueryAlarmNoticeDetailListForm form) {
        log.info("queryAlarmNoticeDetailList start param:{}",form);
        this.formValidate(form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = noticeService.queryAlarmNoticeDetailList(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询报警通知详情列表失败");
            log.error(e.getMessage(), e);
        }
        log.info("queryAlarmNoticeDetailList end return:{}",result.getData());
        return result;
    }

    /**
     * @Description：查询报警通知接收设置
     * @author YZL
     * @data 2021/6/30 11:07
     */
    @PostMapping(value = "/queryAlarmNoticeReceiveSet")
    @ApiOperation("报警推送设置界面")
    @SneakyThrows
    @LoginRequired
    public HttpCommandResultWithData queryAlarmNoticeReceiveSet(@ApiIgnore @RequestJson QueryAlarmNoticeReceiveSetForm form) {
        log.info("queryAlarmNoticeReceiveSet start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = noticeService.queryNewAlarmNoticeReceiveSet(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询报警通知接收设置失败");
            log.error(e.getMessage(), e);
        }
        log.info("queryAlarmNoticeReceiveSet end return:{}",result.getData());
        return result;
    }

    /**
     * @Description：消息和报警通知接收设置（ios端用）
     * @author YZL
     * @data 2021/7/29 14:48
     */
    @PostMapping(value = "/queryAlarmNoticeReceiveSetIos")
    @ApiOperation("报警推送设置界面(IOS)")
    @SneakyThrows
    @LoginRequired
    public HttpCommandResultWithData queryAlarmNoticeReceiveSetIos(@ApiIgnore @RequestJson QueryAlarmNoticeReceiveSetForm form){
        log.info("queryAlarmNoticeReceiveSet start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = noticeService.queryNewAlarmNoticeReceiveSetIos(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询报警通知接收设置失败");
            log.error(e.getMessage(), e);
        }
        log.info("queryAlarmNoticeReceiveSetIos end return:{}",result.getData());
        return result;
    }

    /**
     * @Description：消息和报警通知接收设置
     * @author YZL
     * @data 2021/6/30 11:14
     */
    @PostMapping(value = "/updateReceiveSet")
    @ApiOperation("报警推送设置")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "messageType" , value = "消息类型" , dataType = "Integer"),
            @ApiImplicitParam(name = "receiveState" , value = "消息接收状态" , dataType = "Integer")
    })
    @LoginRequired
    @SneakyThrows
    public HttpCommandResultWithData updateReceiveSet(@ApiIgnore @RequestJson UpdateReceiveSetForm form) {
        log.info("updateReceiveSet start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = noticeService.updateNewReceiveSet(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("消息和报警通知接收设置失败");
            log.error(e.getMessage(), e);
        }
        log.info("updateReceiveSet end return:{}",result.getData());
        return result;
    }

    @PostMapping("/addUnreadMessage")
    @SneakyThrows
    public HttpCommandResultWithData addUnreadMessage(@ApiIgnore @RequestJson AddUnreadMessageForm form){
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            //新增卡友圈消息内容
            result = noticeService.addUnreadMessage(form);
        }catch (Exception e){
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("消息和报警通知接收设置失败");
            log.error(e.getMessage(),e);
        }
        return result;
    }


}
