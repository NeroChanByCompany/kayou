package com.nut.servicestation.common.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author liuBing
 * @Classname RestTemplateConfig
 * @Description TODO
 * @Date 2021/7/6 15:36
 */
@Configuration
public class RestTemplateConfig {
    /**
     * LoadBalanced 开启rest的负载均衡
     * @return
     */
    @Bean
    //@LoadBalanced
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
