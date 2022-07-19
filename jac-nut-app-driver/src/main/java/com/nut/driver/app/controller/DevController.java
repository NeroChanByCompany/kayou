package com.nut.driver.app.controller;

import cn.hutool.core.date.DateUtil;
import com.nut.common.annotation.RequestJson;
import com.nut.driver.app.form.UserCarAddForm;
import com.nut.common.annotation.LoginRequired;
import com.nut.common.base.BaseController;
import com.nut.common.base.BaseForm;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;

import com.nut.driver.common.component.TokenComponent;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: 开发测试用Controller
 * @author: hcb
 * @createTime: 2021/01/20 20:56
 * @version:1.0
 */
@RestController
@RequestMapping("/dev")
@Api(tags = "后台开发内部接口")
@Slf4j
public class DevController extends BaseController {

    @Autowired
    private TokenComponent tokenComponent;

    /**
     * 服务版本信息，每次发版时，修改此值
     */
    private static final String APP_INFO = "1.0.0_20210607";

    @Resource
    HttpServletRequest request;

    /**
     * 获取服务版本信息
     *
     * @return
     */
    @GetMapping("/getAppInfo")
    @ApiIgnore
    public HttpCommandResultWithData getAppInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("version", APP_INFO);
        map.put("time", DateUtil.now());
        map.put("zone", ZoneId.systemDefault().getId());
        return Result.ok(map);
    }


    /**
     * 获取服务版本信息
     *
     * @return
     */
    @GetMapping("/getVersion/{userId}/{verSion}")
    @ApiIgnore
    public HttpCommandResultWithData getVersion(@PathVariable("userId") String userId, @PathVariable("verSion") String verSion) {
        Map<String, String> map = new HashMap<>();
        map.put("version", verSion);
        map.put("userId", userId);
        map.put("oldVersion",request.getHeader("verSion"));
        return Result.ok(map);
    }

    @GetMapping("/login")
    @ApiIgnore
    public HttpCommandResultWithData login(@RequestParam(name = "account") String account, @RequestParam(name = "pwd") String pwd) {
        log.info("account={},pwd={}", account, pwd);
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", "12345");
        tokenMap.put("userId", "a810053b3ccf6aa9");
        tokenMap.put("appType", "2");
        String token = tokenComponent.generateToken(tokenMap);

        Map<String, String> map = new HashMap<>();
        map.put("time", DateUtil.now());
        map.put("token", token);
        return Result.ok(map);
    }

    @PostMapping("/getAccount")
    @ApiIgnore
    @LoginRequired
    public HttpCommandResultWithData getAccount(@RequestJson BaseForm form) {
        log.info("{}", form.toString());
        String token = form.getToken();
        Map<Object, Object> tokenMap = tokenComponent.getTokenInfo(token);
        Map<String, Object> map = new HashMap<>();
        map.put("time", DateUtil.now());
        map.put("tokenInfo", tokenMap);
        return Result.ok(map);
    }

    @PostMapping("/user/car/add")
    @ApiIgnore
    @LoginRequired
    public HttpCommandResultWithData userCarAdd(@RequestJson UserCarAddForm form) throws Exception {
        log.info("{}", form.toString());
        this.formValidate(form);
        return Result.ok();
    }


}
