package cn.edu.xmu.echochat.Bo;

import cn.edu.xmu.echochat.config.ActiveMQConfig;
import cn.edu.xmu.echochat.config.CustomMessageConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.jms.Destination;
import jakarta.jms.Queue;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.config.JmsListenerEndpointRegistry;
import org.springframework.jms.core.JmsMessagingTemplate;

import java.io.IOException;

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
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        session.getBasicRemote().sendText(objectMapper.writeValueAsString(message));
    }

    public String getQueueName() {
        return this.queueName;
    }
}
