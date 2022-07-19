package com.nut.driver.common.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.nut.common.annotation.impl.RequestJsonHandlerMethodArgumentResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;


import java.nio.charset.Charset;
import java.util.List;

/**
 * TODO
 * @description: Swagger2配置
 * @author: hcb
 * @createTime: 2021/03/30 19:37
 * @version:1.0
 */
@Configuration
@EnableSwagger2WebMvc
@EnableKnife4j
public class Knife4jConfiguration {

    @Bean(value = "apiV1")
    public Docket apiV1() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder()
                        .title("[江淮轻卡车联网]项目[driver]API")
                        .description("基于Swagger2和Knife4j实现的接口文档")
                        .version("1.0")
                        .build())
                .groupName("v1")
                .select()
                // 这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.nut.driver.app.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }
}
