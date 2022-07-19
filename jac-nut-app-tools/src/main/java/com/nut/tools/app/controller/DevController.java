package com.nut.tools.app.controller;

import com.nut.tools.common.constans.CommonConstants;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liuBing
 * @Classname ToolsController
 * @Description TODO
 * @Date 2021/6/22 17:44
 */
@Slf4j
@RestController
@Api(tags = "极光推送相关")
@RequestMapping("/dev")
public class DevController {

    /**
     * 获取服务版本信息
     *
     * @return
     */
    @GetMapping("/getAppInfo")
    public Map getAppInfo() {
        Map<String, Object> map = new HashMap<>();
        map.put("version", CommonConstants.APP_INFO);
        map.put("time", System.currentTimeMillis());
        map.put("zone", ZoneId.systemDefault().getId());
        return map;
    }

}
