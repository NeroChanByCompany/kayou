package com.nut.jac.kafka.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author liuBing
 * @Classname KafkaConfig
 * @Description TODO
 * @Date 2021/9/1 11:14
 */
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.producer.bootstrap-servers:localhost:9092}")
    private String producerServers;
    @Value("${spring.kafka.producer.retries:0}")
    private int retries;
    @Value("${spring.kafka.producer.batch.size:200}")
    private int batchSize;
    @Value("${spring.kafka.producer.linger.ms:1}")
    private int lingerMs;
    @Value("${spring.kafka.producer.buffer.memory:33554432}")
    private int bufferMemory;
    @Value("${spring.kafka.producer.key-serializer:kafka.serializer.StringEncoder}")
    private String producerKeySerializerClass;
    @Value("${spring.kafka.producer.value-serializer:kafka.serializer.StringEncoder}")
    private String producerValueSerializerClass;
    @Value("${spring.kafka.producer.request.timeout.ms:30000}")
    private int producerRequestTimeoutMs;
    /**
     * ******************kafka生产者配置********************
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>(8);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, producerServers);
        props.put(ProducerConfig.RETRIES_CONFIG, retries);
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, producerKeySerializerClass);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, producerValueSerializerClass);
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, producerRequestTimeoutMs);
        return props;
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
    // *********************kafka生产者配置 end*********************
}
