package com.nut.jac.kafka.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.spring.autoconfigure.MeterRegistryCustomizer;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuBing
 * @Classname MetricsCommonConfig
 * @Description TODO 对接监控服务
 * @Date 2021/9/15 17:21
 */
@Configuration
public class MetricsCommonConfig {
    @Value("${spring.application.name}")
    private String serverName;
    @Value("${nut.monitor.profile}")
    private String serverEnv;

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config()
                .commonTags("application", serverName)
                .commonTags("env", serverEnv);
    }


}
