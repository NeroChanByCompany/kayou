package com.nut.tripanalysis.app.controller;

import cn.hutool.core.date.DateUtil;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.common.result.Result;
import com.nut.tripanalysis.common.constants.CommonConstants;
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
 * @Description TODO
 * @Date 2021/6/28 17:11
 */
@RestController
@RequestMapping("/dev")
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
