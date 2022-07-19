package com.nut.common.startup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ApplicationStartup implements CommandLineRunner {

    @Value("${spring.application.name}")
    private String applicationName;

    private final static Logger log = LoggerFactory.getLogger(ApplicationStartup.class);

    @Override
    public void run(String... strings) throws Exception {
        log.info("-------------------- [{}]初始化完成 --------------------", applicationName);
    }
}
