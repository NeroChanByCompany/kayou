package com.nut.driver.app.controller;

import brave.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.dozermapper.core.Mapper;
import com.nut.common.annotation.RequestJson;
import com.nut.common.exception.ExceptionUtil;
import com.nut.common.result.ECode;
import com.nut.common.utils.RedisComponent;
import com.nut.driver.app.dto.RegisterDTO;
import com.nut.driver.app.entity.UserEntity;
import com.nut.driver.app.form.*;
import com.nut.driver.app.service.ModifyUserInfoService;
import com.nut.driver.app.service.UserService;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.driver.common.component.RsaComponent;
import com.nut.driver.common.constants.CommonConstants;
import com.nut.driver.common.constants.RedisConstant;
import com.nut.driver.common.utils.HttpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Case;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Description: 用户服务相关
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.controller
 * @Author: yzl
 * @CreateTime: 2021-06-16 13:22
 * @Email: yzl379131121@163.com
 * @Version: 1.0
 */
@Slf4j
@RestController
//@RequestMapping("/app")
@Api(tags = "用户登录具体相关操作")
public class AppLoginController extends BaseController {

    @Resource
    private UserService userService;
    @Resource
    private ModifyUserInfoService modifyUserInfoService;
    @Resource
    Mapper convert;
    @Resource
    RsaComponent rsaComponent;
    @Resource
    RedisComponent redisComponent;
    @Value("${register.private.key}")
    private String privateKey;

    @Resource
    HttpServletRequest request;


    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 用户登录
     * @Date 21:11 2021/6/16
     * @Param [form]
     **/
    @SneakyThrows
    @ApiOperation(value = "用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "String"),
            @ApiImplicitParam(name = "deviceType", value = "设备类型不能为空", dataType = "String"),
            @ApiImplicitParam(name = "sendMessageKey", value = "短信验证码", dataType = "String"),
            @ApiImplicitParam(name = "appType", value = "登录APP类型", dataType = "String"),
    })
    @Deprecated
    @PostMapping(value = {"/login"})
    public HttpCommandResultWithData login(@ApiIgnore @RequestJson AppLoginForm form) {
        log.info("login start param:{}", form);
        if (StringUtils.isNotBlank(form.getSmsCode())) {
            ExceptionUtil.result(ECode.APP_UPDATE_ERROR.code(), ECode.APP_UPDATE_ERROR.message());
        }
        String version = request.getHeader("version");
        if (StringUtils.isNotBlank(version)) {
            form.setVersion(version);
        }
        return Result.ok(userService.appLogin(form));
    }

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 用户注册
     * @Date 20:41 2021/6/16
     * @Param [form]
     **/
    @PostMapping("/register")
    @SneakyThrows
    @ApiOperation(value = "用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户名", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "String"),
            @ApiImplicitParam(name = "confirmPassword", value = "确认密码", dataType = "String"),
            @ApiImplicitParam(name = "smsCode", value = "短信验证码", dataType = "String"),
            @ApiImplicitParam(name = "appType", value = "登录APP类型", dataType = "String"),
            @ApiImplicitParam(name = "deviceId", value = "设备编号", dataType = "String"),
    })
    @Deprecated
    public HttpCommandResultWithData register(@ApiIgnore @RequestJson RegisterForm form) {
        log.info("register start param:{}", form);
        return Result.result(ECode.APP_UPDATE_ERROR.code(), ECode.APP_UPDATE_ERROR.message());
      /*  if (Objects.nonNull(redisComponent.get(RedisConstant.INTERFACE_ACCESS_RESTRICTION + form.getPhone()))) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "当前操作过于频繁，请稍后再试!");
        }
        redisComponent.set(RedisConstant.INTERFACE_ACCESS_RESTRICTION + form.getPhone(), form.getPhone(), 10);
        return Result.ok(userService.register(form));*/
    }

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 获取短信验证码
     * @Date 20:41 2021/6/16
     * @Param [form]
     **/
    @PostMapping("/smsVerificationCode")
    @SneakyThrows
    @ApiOperation(value = "获取短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机或", dataType = "String"),
            @ApiImplicitParam(name = "sign", value = "验证码请求类型", dataType = "String"),
    })
    @Deprecated
    public HttpCommandResultWithData smsVerificationCode(@ApiIgnore @RequestJson SmsVerificationCodeForm form) {
        log.info("register smsVerificationCode param:{}", form);
       // return Result.result(ECode.APP_UPDATE_ERROR.code(), ECode.APP_UPDATE_ERROR.message());
        userService.smsVerificationCode(form);
        return Result.ok();
    }

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 校验短信验证码
     * @Date 10:20 2021/6/17
     * @Param [form]
     **/
    @PostMapping(value = "/checkSmsCode")
    @LoginRequired
    @ApiOperation(value = "校验短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "smsCode", value = "验证码", dataType = "String"),
    })
    @Deprecated
    public HttpCommandResultWithData checkSmsCode(@ApiIgnore @RequestJson ModifyUserInfoForm form) {
        log.info(" checkSmsCode start param:{}", form);
        return Result.result(ECode.APP_UPDATE_ERROR.code(), ECode.APP_UPDATE_ERROR.message());
    }

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 登出
     * @Date 10:20 2021/6/17
     * @Param [form]
     **/
    @PostMapping(value = "/logout")
    @LoginRequired
    @ApiOperation(value = "登出")
    public HttpCommandResultWithData logout(@ApiIgnore @RequestJson AppLogoutForm form) {
        log.info(" logout start param:{}", form);
        userService.logout(form);
        return Result.ok();
    }

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 忘记密码
     * @Date 10:20 2021/6/17
     * @Param [form]
     **/
    @PostMapping(value = "/forgetPassword")
    @SneakyThrows
    @ApiOperation(value = "忘记密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "smsCode", value = "短信验证码", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "String"),
            @ApiImplicitParam(name = "confirmPassword", value = "确认密码", dataType = "String"),
            @ApiImplicitParam(name = "appType", value = "登录APP类型", dataType = "String"),
    })
    @Deprecated
    public HttpCommandResultWithData forgetPassword(@ApiIgnore @RequestJson ForgetPasswordForm form) {
        log.info(" forgetPassword start param:{}", form);
        return Result.result(ECode.APP_UPDATE_ERROR.code(), ECode.APP_UPDATE_ERROR.message());
    }


    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 修改密码
     * @Date 10:20 2021/6/17
     * @Param [form]
     **/
    @PostMapping(value = "/updatePassword")
    @SneakyThrows
    @LoginRequired
    @ApiOperation(value = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPassword", value = "旧密码", dataType = "String"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", dataType = "String"),
    })
    @Deprecated
    public HttpCommandResultWithData updatePassword(@ApiIgnore @RequestJson UpdatePasswordForm form) {
        log.info(" updatePassword start param:{}", form);
        return Result.result(ECode.APP_UPDATE_ERROR.code(), ECode.APP_UPDATE_ERROR.message());
    }

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 修改用户信息
     * @Date 15:43 2021/6/17
     * @Param [form]
     **/
    @PostMapping("/modifyUserInfo")
    @LoginRequired
    @ApiOperation(value = "修改用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户名", dataType = "String"),
            @ApiImplicitParam(name = "userPicUrl", value = "头像地址", dataType = "String"),
            @ApiImplicitParam(name = "signature", value = "签名", dataType = "String"),
            @ApiImplicitParam(name = "sex", value = "性别", dataType = "String"),
            @ApiImplicitParam(name = "interest", value = "兴趣", dataType = "String"),
            @ApiImplicitParam(name = "birthday", value = "生日", dataType = "Date"),
            @ApiImplicitParam(name = "provinceDesc", value = "省份/直辖市", dataType = "String"),
            @ApiImplicitParam(name = "cityDesc", value = "地级市", dataType = "String"),
            @ApiImplicitParam(name = "drivingAge", value = "驾龄", dataType = "Integer"),
            @ApiImplicitParam(name = "email", value = "邮箱", dataType = "String"),
            @ApiImplicitParam(name = "annualIncome", value = "年收入单位（万元）", dataType = "Integer"),
            @ApiImplicitParam(name = "orgCode", value = "组织代码", dataType = "String"),
            @ApiImplicitParam(name = "phone", value = "用户手机号", dataType = "String"),
            @ApiImplicitParam(name = "smsCode", value = "短信验证码", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "用户类型", dataType = "String"),
            @ApiImplicitParam(name = "realName", value = "真实名称", dataType = "String"),
    })
    public HttpCommandResultWithData modifyUserInfo(@ApiIgnore @RequestJson ModifyUserInfoForm form) {
        log.info(" modifyUserInfo start param:{}", form);
        userService.modifyUserInfo(form);
        return Result.ok();
    }


    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 修改手机号
     * @Date 15:44 2021/6/17
     * @Param [form]
     **/
    @PostMapping("/modifyPhone")
    @LoginRequired
    @ApiOperation(value = "修改手机号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "type类型", dataType = "String"),
    })
    @Deprecated
    public HttpCommandResultWithData modifyPhone(@ApiIgnore @RequestJson ModifyUserInfoForm form) {
        log.info("modifyPhone start param:{}", form);
        return Result.result(ECode.APP_UPDATE_ERROR.code(), ECode.APP_UPDATE_ERROR.message());
    }


    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 用户头像上传
     * @Date 15:44 2021/6/17
     * @Param [form]
     **/
    @SneakyThrows
    @PostMapping("/upLoadUserPicUrl")
    @LoginRequired
    @ApiOperation(value = "用户头像上传")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userPicUrl", value = "头像地址", dataType = "String"),
    })
    public HttpCommandResultWithData uploadUserPicUrl(@ApiIgnore @RequestJson UploadUserPicUrlForm form) {
        log.info("uploadUserPicUrl start param:{}", form);
        this.formValidate(form);
        return Result.ok(userService.uploadUserPicUrl(form));
    }

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 查询用户信息
     * @Date 15:44 2021/6/17
     * @Param [form]
     **/
    @SneakyThrows
    @PostMapping("/getUserInfo")
    @LoginRequired
    @ApiOperation(value = "查询用户信息")
    public HttpCommandResultWithData getUserInfo(@ApiIgnore @RequestJson GetUserInfoForm form) {
        log.info("getUserInfo start param:{}", form);
        return Result.ok(userService.getUserInfo(form));
    }

    @PostMapping(value = "/updateUserSignature")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "更新用户签名")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "signature", value = "签名", dataType = "String"),
    })
    public HttpCommandResultWithData updateUserSignature(@ApiIgnore @RequestJson UpdateUserSignatureForm form) {
        log.info("updateUserSignature start param:{}", form);
        this.formValidate(form);
        return Result.ok(userService.updateUserSignature(form));
    }


    @PostMapping(value = "/cancellation")
    @LoginRequired
    @SneakyThrows
    @ApiOperation(value = "注销当前账户")
    public HttpCommandResultWithData cancellation(@ApiIgnore @RequestJson AppLogoutForm form) {
        log.info("cancellation start param:{}", form);
        userService.cancellation(form);
        return Result.ok();
    }


    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 用户注册
     * @Date 20:41 2021/6/16
     * @Param [form]
     **/
    @PostMapping("/v2/register")
    @SneakyThrows
    @ApiOperation(value = "用户注册")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "用户名", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "String"),
            @ApiImplicitParam(name = "confirmPassword", value = "确认密码", dataType = "String"),
            @ApiImplicitParam(name = "smsCode", value = "短信验证码", dataType = "String"),
            @ApiImplicitParam(name = "appType", value = "登录APP类型", dataType = "String"),
            @ApiImplicitParam(name = "deviceId", value = "设备编号", dataType = "String"),
            @ApiImplicitParam(name = "sign", value = "加密签名", dataType = "String"),
    })
    public HttpCommandResultWithData v2Register(@ApiIgnore @RequestJson RegisterForm form) {
        log.info("register start param:{}", form);
        this.formValidate(form);
        if (checkPhone(form.getSign(), form.getPhone())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "用户信息校验失败，请重新输入");
        }
        form.setVersionType(request.getHeader("versionType"));
        form.setVersion(request.getHeader("version"));
        form.setRegisterFlag(1);
        return Result.ok(userService.register(form));
    }


    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 用户登录
     * @Date 21:11 2021/6/16
     * @Param [form]
     **/
    @SneakyThrows
    @ApiOperation(value = "用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "String"),
            @ApiImplicitParam(name = "deviceType", value = "设备类型不能为空", dataType = "String"),
            @ApiImplicitParam(name = "sendMessageKey", value = "短信验证码", dataType = "String"),
            @ApiImplicitParam(name = "appType", value = "登录APP类型", dataType = "String"),
            @ApiImplicitParam(name = "sign", value = "登录签名", dataType = "String"),
    })
    @PostMapping(value = {"/v2/login"})
    public HttpCommandResultWithData v2Login(@ApiIgnore @RequestJson AppNewLoginForm form) {
        log.info("login start param:{}", form);
        this.formValidate(form);
        if (checkPhone(form.getSign(), form.getPhone())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "用户信息校验失败，请重新输入");
        }
        String version = request.getHeader("version");
        if (StringUtils.isNotBlank(version)) {
            form.setVersion(version);
        }
        AppLoginForm map = convert.map(form, AppLoginForm.class);
        return Result.ok(userService.appLogin(map));
    }

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 获取短信验证码
     * @Date 20:41 2021/6/16
     * @Param [form]
     **/
    @PostMapping("/v2/smsVerificationCode")
    @SneakyThrows
    @ApiOperation(value = "获取短信验证码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "sign", value = "验证码请求类型", dataType = "String"),
            @ApiImplicitParam(name = "secret", value = "秘钥", dataType = "String"),
    })
    public HttpCommandResultWithData v2SmsVerificationCode(@ApiIgnore @RequestJson SmsVerificationCodeForm form) {
        log.info("register smsVerificationCode param:{}", form);
        this.formValidate(form);
        if (checkPhone(form.getSecret(), form.getPhone())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "用户信息校验失败，请重新输入");
        }
        userService.smsVerificationCode(form);
        return Result.ok();
    }

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 忘记密码
     * @Date 10:20 2021/6/17
     * @Param [form]
     **/
    @PostMapping(value = "/v2/forgetPassword")
    @SneakyThrows
    @ApiOperation(value = "忘记密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "smsCode", value = "短信验证码", dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "String"),
            @ApiImplicitParam(name = "confirmPassword", value = "确认密码", dataType = "String"),
            @ApiImplicitParam(name = "appType", value = "登录APP类型", dataType = "String"),
            @ApiImplicitParam(name = "sign", value = "加密签名", dataType = "String"),
    })
    public HttpCommandResultWithData v2ForgetPassword(@ApiIgnore @RequestJson ForgetPasswordForm form) {
        log.info(" forgetPassword start param:{}", form);
        this.formValidate(form);
        if (checkPhone(form.getSign(), form.getPhone())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "用户信息校验失败，请重新输入");
        }
        return Result.ok(userService.forgetPassword(form));
    }

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 修改密码
     * @Date 10:20 2021/6/17
     * @Param [form]
     **/
    @PostMapping(value = "/v2/updatePassword")
    @SneakyThrows
    @LoginRequired
    @ApiOperation(value = "修改密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPassword", value = "旧密码", dataType = "String"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", dataType = "String"),
            @ApiImplicitParam(name = "sign", value = "加密签名", dataType = "String"),
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
    })
    public HttpCommandResultWithData v2UpdatePassword(@ApiIgnore @RequestJson UpdatePasswordForm form) {
        log.info(" updatePassword start param:{}", form);
        this.formValidate(form);
        if (checkPhone(form.getSign(), form.getPhone())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "用户信息校验失败，请重新输入");
        }
        return Result.ok(userService.updatePassword(form));
    }

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 修改手机号
     * @Date 15:44 2021/6/17
     * @Param [form]
     **/
    @PostMapping("/v2/modifyPhone")
    @LoginRequired
    @ApiOperation(value = "修改手机号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String"),
            @ApiImplicitParam(name = "type", value = "type类型", dataType = "String"),
            @ApiImplicitParam(name = "sign", value = "加密签名", dataType = "String"),
            @ApiImplicitParam(name = "smsCode", value = "短信验证码", dataType = "String"),
    })
    public HttpCommandResultWithData v2ModifyPhone(@ApiIgnore @RequestJson ModifyPhoneForm form) throws Exception {
        log.info("modifyPhone start param:{}", form);
        this.formValidate(form);
        if (checkPhone(form.getSign(), form.getPhone())) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "用户信息校验失败，请重新输入");
        }
        userService.modifyPhone(form);
        return Result.ok();
    }

    @PostMapping("/starSign")
    @LoginRequired
    @ApiOperation(value = "用户网红显示标识")
    public HttpCommandResultWithData starSign(StarSignForm form){
        return Result.ok(userService.starSign(form));
    }


    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author liuBing
     * @Description //TODO 根据手机号查询用户信息
     * @Date 15:44 2021/6/17
     * @Param [form]
     **/
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String", required = true),
    })
    @SneakyThrows
    @PostMapping("/getUserByPhone")
    @ApiOperation(value = "查询用户信息")
    public HttpCommandResultWithData getUserByPhone(@ApiIgnore @RequestBody GetUserPhoneForm form) {
        log.info("getUserInfo start param:{}", form);
        this.formValidate(form);
        return Result.ok(userService.getOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getPhone, form.getPhone())
                .eq(UserEntity::getAppType, 0).last("limit 1")));
    }

    /**
     * @return com.nut.driver.common.result.HttpCommandResultWithData
     * @Author yzl
     * @Description //TODO 根据手机号查询用户信息   直销商城专用
     * @Date 15:44 2021/11/4
     * @Param [form]
     **/
    @ApiImplicitParams({
            @ApiImplicitParam(name = "phone", value = "手机号", dataType = "String", required = true),
            @ApiImplicitParam(name = "type", value = "app类型", dataType = "String", required = true)
    })
    @SneakyThrows
    @PostMapping("/getUserByPhone2")
    @ApiOperation(value = "查询用户信息")
    public HttpCommandResultWithData getUserByPhone2(@ApiIgnore @RequestBody GetUserPhone2Form form) {
        log.info("getUserInfo start param:{}", form);
        this.formValidate(form);
        switch (form.getType()){
            case "0": ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "未知app用户类型"); break;
            case "1": form.setType("0"); break;
            case "2": form.setType("1"); break;

        }
        return Result.ok(userService.getOne(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getPhone, form.getPhone())
                .eq(UserEntity::getAppType, form.getType()).last("limit 1")));
    }

    /**
     * 校验手机号和签名 不正确返true
     *
     * @param sign  签名
     * @param phone 手机号
     * @return
     */
    protected boolean checkPhone(String sign, String phone) {
        String newPhone = "jac+" + phone;
        String newSign = "";
        try {
            newSign = rsaComponent.decrypt(sign, privateKey);
        } catch (Exception e) {
            ExceptionUtil.result(ECode.CLIENT_ERROR.code(), "用户信息校验失败，请重新输入");
        }

        return !newSign.equals(newPhone);
    }
}
