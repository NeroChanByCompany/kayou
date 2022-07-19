package com.nut.locationservice.app.controller;

import cn.hutool.core.date.DateUtil;
import com.nut.locationservice.common.constants.CommonConstants;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuBing
 * @Classname DevController
 * @Description TODO 获取服务标识
 * @Date 2021/6/16 9:54
 */
@RestController
@RequestMapping("/dev")
@Api(tags = "后台开发内部接口")
@Slf4j
public class DevController {

    /**
     * 获取服务版本信息
     *
     * @return
     */
    @GetMapping("/getAppInfo")
    @ApiIgnore
    public HttpCommandResultWithData getAppInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("version", CommonConstants.APP_INFO);
        map.put("time", DateUtil.now());
        map.put("zone", ZoneId.systemDefault().getId());
        return Result.ok(map);
    }
}
