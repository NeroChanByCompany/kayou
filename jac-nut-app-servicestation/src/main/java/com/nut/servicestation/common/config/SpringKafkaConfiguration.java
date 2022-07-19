
/*
package com.nut.servicestation.common.config;

import com.nut.servicestation.app.kafka.SyncKafKaInOutAreaListener;
import com.nut.servicestation.app.kafka.SyncKafKaInOutAreaToCrmListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.MessageListener;


import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonSerializer;


import java.util.HashMap;
import java.util.Map;

*/
/**
 * kafka本地化配置
 * <p>
 * <kafka_2.11.version>0.9.0.1</kafka_2.11.version>
 * <spring-kafka.version>1.0.3.RELEASE</spring-kafka.version>
 *
 * @author admin
 */
/*
@Deprecated
@Configuration
public class SpringKafkaConfiguration {

    @Value("${kafka.producer.topic.crm.synchronize.info}")
    protected String[] crmTopic;
    @Value("${kafka.consumer.topic.area.synchronize.info}")
    protected String[] inOutAreaTopic;
    @Value("${spring.kafka.producer.servers:localhost:9092}")
    private String producerServers;
    @Value("${spring.kafka.producer.key.serializer.class:kafka.serializer.StringEncoder}")
    private String producerKeySerializerClass;
    @Value("${spring.kafka.producer.value.serializer.class:kafka.serializer.StringEncoder}")
    private String producerValueSerializerClass;
    @Value("${spring.kafka.producer.batch.size:200}")
    private int batchSize;
    @Value("${spring.kafka.producer.buffer.memory:33554432}")
    private int bufferMemory;
    @Value("${spring.kafka.producer.request.timeout.ms:30000}")
    private int producerRequestTimeoutMs;
    @Value("${spring.kafka.producer.retries:0}")
    private int retries;
    @Value("${spring.kafka.producer.linger.ms:1}")
    private int lingerMs;

    @Value("${spring.kafka.consumer.servers:localhost:9092}")
    private String bootstrapServers;
    @Value("${spring.kafka.consumer.request.timeout.ms:30000}")
    private int consumerRequestTimeoutMs;
    @Value("${spring.kafka.consumer.concurrency.size:1}")
    private int concurrencySize;
    @Value("${spring.kafka.consumer.session.timeout.ms:15000}")
    private String sessionTimeoutMs;
    @Value("${spring.kafka.consumer.enable.auto.commit:true}")
    private boolean enableAutoCommit;
    @Value("${spring.kafka.consumer.auto.commit.interval.ms:500}")
    private String autoCommitIntervalMs;
    @Value("${spring.kafka.consumer.client.id}")
    private String clientId;
    @Value("${spring.kafka.consumer.group.id}")
    private String groupId;
    @Value("${spring.kafka.consumer.crm.group.id}")
    private String crmGroupId;
    @Value("${spring.kafka.consumer.key.serializer.class:kafka.serializer.StringEncoder}")
    private String consumerKeySerializerClass;
    @Value("${spring.kafka.consumer.value.serializer.class:kafka.serializer.StringEncoder}")
    private String consumerValueSerializerClass;
    @Value("${spring.kafka.consumer.auto.offset.reset}")
    protected String autoReset;
    @Value("${spring.kafka.consumer.max.poll.records:10}")
    private String maxPollRecords;
    @Value("${spring.kafka.consumer.max.poll.interval.ms:600000}")
    private String maxPollIntervalMs;
    @Value("${webservice.crm.inOutAreaToCrm.enable:true}")
    private boolean enableInOutAreaToCrm;
    @Autowired
    private SyncKafKaInOutAreaListener syncKafKaInOutAreaListener;
    @Autowired
    private SyncKafKaInOutAreaToCrmListener syncKafKaInOutAreaToCrmListener;
*/
/**
     * ******************kafka消费者配置********************
     *//*


    private ConsumerFactory<String, Object> consumerFactory(String[] topic, String groupId) {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(topic, groupId));
    }

    private Map<String, Object> consumerConfigs(String[] topic, String groupId) {
        Map<String, Object> props = new HashMap<>(10);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.CLIENT_ID_CONFIG, clientId);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, autoCommitIntervalMs);
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerKeySerializerClass);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerValueSerializerClass);
        props.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, consumerRequestTimeoutMs);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoReset);
        if (topic != null && topic.length > 0 && crmTopic != null && crmTopic.length > 0 && crmTopic[0].equals(topic[0])) {
            props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
            props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
        }
        return props;
    }

    private ContainerProperties containerProperties(String[] topic, MessageListener messageListener) {
        ContainerProperties containerProperties = new ContainerProperties(topic);
        containerProperties.setMessageListener(messageListener);
        return containerProperties;
    }

    private void createContainer(String[] topic, MessageListener messageListener, String groupId, int nameIndex) {
        ConcurrentMessageListenerContainer container = new ConcurrentMessageListenerContainer<>(
                consumerFactory(topic, groupId), containerProperties(topic, messageListener));
        container.setConcurrency(concurrencySize);
        container.getContainerProperties().setPollTimeout(consumerRequestTimeoutMs);
        // 消费线程名使用到BeanName
        container.setBeanName("serviceStationContainer" + nameIndex);
        container.start();
    }

    private void createContainer(String[] topic, MessageListener messageListener, int nameIndex) {
        this.createContainer(topic, messageListener, groupId, nameIndex);
    }

*/
/**
     * 由于kafka消费者消费实例启动和springboot的启动不再同一个线程里启动，所以等springboot容器启动完成后，在启动kafka，以避免
     * 在kafka线程先启动消费的时候调用spring容器还没有加载完的类实例,所以此处的启动挪到Application启动类的run方法里调用
     *//*

    public void start() {
        //消费工单信息同步CRM数据信息
        createContainer(inOutAreaTopic, syncKafKaInOutAreaListener, 1);
        if(this.enableInOutAreaToCrm){
            createContainer(inOutAreaTopic, syncKafKaInOutAreaToCrmListener, crmGroupId, 2);
        }
    }



    // *********************kafka消费者配置 end*********************

    */
/**
     * ******************kafka生产者配置********************
     *//*


    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>(3);
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
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
*/
