package com.nut.driver.app.client.fallback;


import com.alibaba.fastjson.JSONObject;
import com.nut.driver.app.client.TspClient;
import org.springframework.stereotype.Component;

/**
 * @author liuBing
 * @Classname TspFallback
 * @Description TODO
 * @Date 2021/8/3 13:44
 */
@Component
public class TspFallback implements TspClient {
    @Override
    public JSONObject queryOilFaultApp(String chassisNum, String Datestr, Integer page_size, Integer page_number) {
        return null;
    }

    @Override
    public JSONObject queryOilExceptionAppUp(String chassisNum, String Datestr, Integer page_size, Integer page_number) {
        return null;
    }

    @Override
    public JSONObject queryOilFaultAppDown(String chassisNum, String Datestr, Integer page_size, Integer page_number) {
        return null;
    }
}
