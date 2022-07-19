package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.base.BaseController;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.UserContactAddForm;
import com.nut.driver.app.form.UserContactDeleteForm;
import com.nut.driver.app.form.UserContactsForm;
import com.nut.driver.app.service.ContactService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@RestController
@Api(value = "联系人接口")
public class ContactController extends BaseController {

    @Autowired
    private ContactService contactService;

    /**
     * 用户添加联系人接口(添加司机端数据到司机)
     */
    @PostMapping(value = "/user/contact/add")
    @ApiOperation("新增司机")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "nickname", value = "昵称", dataType = "String"),
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String")
    })
    @LoginRequired
    public HttpCommandResultWithData userContactAdd(@RequestJson UserContactAddForm form) {
        log.info("userContactAdd start param:{}", form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = contactService.add(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("提交失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userContactAdd end return:{null}");
        return result;
    }

    /**
     * 用户删除联系人接口(司机)
     */
    @PostMapping(value = "/user/contact/delete")
    @ApiOperation("删除司机")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "delUserId", value = "联系人用户ID", dataType = "Long")
    })
    @LoginRequired
    public HttpCommandResultWithData userContactDelete(@RequestJson UserContactDeleteForm form) {
        log.info("userContactDelete start param:{}", form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            this.formValidate(form);
            result = contactService.delete(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("删除失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userContactDelete end return:{null}");
        return result;
    }

    /**
     * 用户联系人列表接口(司机)
     */
    @PostMapping(value = "/user/contacts")
    @ApiOperation("查询司机列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "关键字", dataType = "String"),
            @ApiImplicitParam(name = "exclusiveTeamId", value = "排除的车队ID", dataType = "Long"),
            @ApiImplicitParam(name = "exclusiveRole", value = "排除的车队角色", dataType = "Integer")
    })
    @LoginRequired
    public HttpCommandResultWithData userContacts(@RequestJson UserContactsForm form) {
        log.info("userContacts start param:{}", form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = contactService.query(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("查询失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("userContacts end return:{null}");
        return result;
    }

    /**
     * 用户添加手机联系人接口
     */
    @RequestMapping(value = "/user/contact/addContact")
    @LoginRequired
    public @ResponseBody
    HttpCommandResultWithData addContact( @RequestBody UserContactAddForm form) {
        log.info("[addContact]start");
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = contactService.addContact(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("提交失败，请稍后重试");
            log.error(e.getMessage(), e);
        }
        log.info("[addContact]end");
        return result;
    }
}
