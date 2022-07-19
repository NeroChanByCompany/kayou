package com.nut.tools;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author liuBing
 * @Classname ToolsApplication
 * @Description TODO
 * @Date 2021/6/22 17:19
 */
@Slf4j
@SpringBootApplication
@EnableEurekaClient
@MapperScan(basePackages = {"com.nut.tools.app.mapper"})
public class ToolsApplication {

    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ToolsApplication.class, args);
        ConfigurableEnvironment environment = applicationContext.getEnvironment();
        String port = environment.getProperty("server.port");
        String address = InetAddress.getLocalHost().getHostAddress();
        log.info("\n----------------------------------------------------------\n\t" +
                "Application tools is running! Access URLs:\n\t" +
                "Local: \t\thttp://localhost:" + port + "/\n\t" +
                "External: \thttp://" + address + ":" + port + "/\n\t" +
                "Swagger文档: \thttp://" + address + ":" + port  + "/doc.html\n" +
                "----------------------------------------------------------");
    }
}
