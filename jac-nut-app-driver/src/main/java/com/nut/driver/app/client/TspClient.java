package com.nut.driver.app.client;


import com.alibaba.fastjson.JSONObject;
import com.nut.driver.app.client.fallback.TspFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Description: TSP接口调用
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.client
 * @Author: yzl
 * @CreateTime: 2021-07-22 15:36
 * @Version: 1.0
 */
@FeignClient(value = "tsp-report-service", fallback = TspFallback.class)
public interface TspClient {

    // 总体油耗数据
    @GetMapping(value = "/api/jac/report/oil/queryOilFaultApp")
    JSONObject queryOilFaultApp(@RequestParam("chassisNum") String chassisNum,
                                @RequestParam("Datestr") String Datestr,
                                @RequestParam("page_size") Integer page_size,
                                @RequestParam("page_number") Integer page_number);

    // 加油记录
    @GetMapping("/api/jac/report/oil/queryOilFaultAppUp")
    JSONObject queryOilExceptionAppUp(@RequestParam("chassisNum") String chassisNum,
                                      @RequestParam("Datestr") String Datestr,
                                      @RequestParam("page_size") Integer page_size,
                                      @RequestParam("page_number") Integer page_number
    );

    // 油耗异常减少记录
    @GetMapping("/api/jac/report/oil/queryOilFaultAppDown")
    JSONObject queryOilFaultAppDown(@RequestParam("chassisNum") String chassisNum,
                                    @RequestParam("Datestr") String Datestr,
                                    @RequestParam("page_size") Integer page_size,
                                    @RequestParam("page_number") Integer page_number
    );
}
