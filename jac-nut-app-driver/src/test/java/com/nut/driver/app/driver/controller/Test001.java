package com.nut.driver.app.driver.controller;

import com.alibaba.fastjson.JSONObject;
import com.nut.driver.DriverApplication;
import com.nut.driver.common.component.BaiduMapComponent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.controller
 * @Author: yzl
 * @CreateTime: 2021-08-05 09:42
 * @Version: 1.0
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DriverApplication.class)
public class Test001 {

    @Autowired
    private BaiduMapComponent baiduMapComponent;

    @Test
    @SneakyThrows
    public void test0(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = baiduMapComponent.reverseGeocoding("34.535","108.253","bd09ll");
        }catch (Exception e){

        }
        log.info("json:{}",jsonObject);
    }
}
