package cn.edu.xmu.echochat.Bo;

import cn.edu.xmu.echochat.config.ActiveMQConfig;
import cn.edu.xmu.echochat.config.CustomMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.jms.Destination;
import jakarta.jms.Queue;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsMessagingTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@ConditionalOnProperty(prefix = "spring.activemq.jms", name = "enable", havingValue = "true")
public class OnlineUser {

    private JmsListenerEndpointRegistry registry;
    JmsListenerContainerFactory<?> factory;

    private JmsMessagingTemplate jmsMessagingTemplate;

    private Session session;

    private Queue queue;

    private String queueName;

    private ObjectMapper objectMapper;

    public OnlineUser(Session session, Long userId, JmsMessagingTemplate jmsMessagingTemplate, JmsListenerContainerFactory<?> factory) {
        this.session = session;
        this.queueName = 'u' + userId.toString();
        this.queue = ActiveMQConfig.queue(queueName);
        this.jmsMessagingTemplate = jmsMessagingTemplate;
        this.factory = factory;
        objectMapper = new ObjectMapper();
        // 添加Java 8时间模块并定义LocalDateTime的序列化格式
        JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        objectMapper.registerModule(timeModule);

        // 配置ObjectMapper序列化byte[]为JSON数组
        SimpleModule byteModule = new SimpleModule();
        byteModule.addSerializer(byte[].class, new ByteArraySerializer());
        objectMapper.registerModule(byteModule);

        // 禁用日期时间为时间戳的特性
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        addQueueListener();
    }

    private void addQueueListener() {
        CustomMessageConverter customMessageConverter = new CustomMessageConverter();
        registry = new JmsListenerEndpointRegistry();
        SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setId(queueName + "Endpoint");
        endpoint.setDestination(queueName);
        endpoint.setMessageListener(message -> {
            try {
                Msg msg = (Msg) customMessageConverter.fromMessage(message);
                if (msg instanceof Msg) {   // 根据实际情况进行类型转换和判断
                    readActiveQueue(msg);
                }
            } catch (Exception e) {
                log.error("处理消息时发生错误", e);
            }
        });

        registry.registerListenerContainer(endpoint, factory, true);
    }

    public void sendQueue(Long receiverId, Msg message) {
        Queue receiverQueue = ActiveMQConfig.queue('u' + receiverId.toString());
        sendMessage(receiverQueue, message);
    }

    private void sendMessage(Destination destination, Msg message) {
        jmsMessagingTemplate.convertAndSend(destination, message);
    }

    public void readActiveQueue(Msg message) throws IOException {
        String str = objectMapper.writeValueAsString(message);
        session.getBasicRemote().sendText(str);
    }

    public String getQueueName() {
        return this.queueName;
    }
}
