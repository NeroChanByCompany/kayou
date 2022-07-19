package com.nut.driver;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @description: 服务启动类
 * @author: hcb
 * @createTime: 2021/01/20 10:28
 * @version:1.0
 */
//todo 用于延迟队列，暂时功能隐藏
//@EnableScheduling
@Slf4j
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableAsync
@ComponentScan(basePackages = {"com.nut.common.utils","com.nut.driver", "com.nut.common.assembler","com.nut.common.config"})
public class DriverApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(DriverApplication.class, args);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String port = environment.getProperty("server.port");
        String address = InetAddress.getLocalHost().getHostAddress();
        log.info("\n----------------------------------------------------------\n\t" +
                "Application driver is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + "/\n\t" +
                "External: \thttp://" + address + ":" + port + "/\n\t" +
                "Swagger文档: \thttp://" + address + ":" + port  + "/doc.html\n" +
                "----------------------------------------------------------");
    }

}
