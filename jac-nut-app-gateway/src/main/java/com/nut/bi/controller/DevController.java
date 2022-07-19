package com.nut.bi.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: hcb
 * @createTime: 2021/01/20 20:56
 * @version:1.0
 */
@RestController
@RequestMapping("/dev")
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
    public Map getAppInfo() {
        Map<String, String> map = new HashMap<>();
        map.put("version", APP_INFO);
        map.put("time", String.valueOf(System.currentTimeMillis()));
        map.put("zone", ZoneId.systemDefault().getId());
        return map;
    }
}
