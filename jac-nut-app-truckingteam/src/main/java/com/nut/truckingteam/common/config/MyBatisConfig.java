package com.nut.truckingteam.common.config;

import com.github.pagehelper.PageInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TODO
 * @description: MapperScan路径
 * @author: hcb
 * @createTime: 2021/01/20 10:33
 * @version:1.0
 */
@Configuration
@MapperScan("com.nut.truckingteam.app.dao")
public class MyBatisConfig {

    /**
     * pagehelper的分页插件
     */
    @Bean
    public PageInterceptor pageInterceptor() {
        return new PageInterceptor();
    }
}
