package com.nut.servicestation.common.startup;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @description:
 * @author: hcb
 * @createTime: 2021/02/01 14:29
 * @version:1.0
 */
@Component
@Order(1)
@Slf4j
public class ApplicationStartup implements CommandLineRunner {

    @Value("${spring.application.name}")
    private String applicationName;

    /*@Autowired
    private SpringKafkaConfiguration springKafkaConfiguration;*/

    @Override
    public void run(String... strings) throws Exception {
        log.info("-------------------- [{}]初始化完成 --------------------", applicationName);
        /*springKafkaConfiguration.start();*/
        log.info("-------------------- kafka配置加载完成 --------------------");
    }
}
