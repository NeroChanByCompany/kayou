package com.nut.jac.kafka.controller;

import cn.hutool.core.date.DateUtil;
import com.nut.jac.kafka.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
@Slf4j
public class DevController {


    /**
     * 服务版本信息，每次发版时，修改此值
     */
    private static final String APP_INFO = "1.0.0_20210607";

    /**
     * 获取服务版本信息
     *
     * @return
     */
    @GetMapping("/getAppInfo")
    public Result<Map<String,String>> getAppInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("version", APP_INFO);
        map.put("time", DateUtil.now());
        map.put("zone", ZoneId.systemDefault().getId());
        return Result.ok(map);
    }

}
