package com.nut.locationservice;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author liuBing
 * @Classname LocationServiceController
 * @Description TODO locatioService服务启动类
 * @Date 2021/6/15 22:25
 */
@Slf4j
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.nut.locationservice.app.dao"})
@ComponentScan(basePackages = {"com.nut.locationservice", "com.nut.common.utils"})
public class LocationServiceApplication {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(LocationServiceApplication.class, args);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String port = environment.getProperty("server.port");
        String address = InetAddress.getLocalHost().getHostAddress();
        log.info("\n----------- -----------------------------------------------\n\t" +
                "Application locationService is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + "/\n\t" +
                "External: \thttp://" + address + ":" + port + "/\n\t" +
                "Swagger文档: \thttp://" + address + ":" + port  + "/doc.html\n" +
                "----------------------------------------------------------");
    }
}
