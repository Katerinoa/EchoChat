package cn.edu.xmu.echochat.Bo;

import cn.edu.xmu.echochat.config.ActiveMQConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.Destination;
import jakarta.jms.Queue;
import jakarta.websocket.Session;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;

import java.io.IOException;

@Slf4j
@ConditionalOnProperty(prefix = "spring.activemq.jms", name = "enable", havingValue = "true")
public class OnlineUser {

    private JmsMessagingTemplate jmsMessagingTemplate;

    private Session session;

    private Queue queue;

    private String queueName;
    private ObjectMapper objectMapper;

    public OnlineUser(Session session, Long userId, JmsMessagingTemplate jmsMessagingTemplate) {
        this.session = session;
        this.queueName = 'u' + userId.toString();
        this.queue = ActiveMQConfig.queue(queueName);
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }

    public void sendQueue(Long receiverId, Msg message) {
        Queue receiverQueue = ActiveMQConfig.queue('u' + receiverId.toString());
        sendMessage(receiverQueue, message);
    }

    private void sendMessage(Destination destination, Msg message) {
        jmsMessagingTemplate.convertAndSend(destination, message);
    }

    @JmsListener(destination = "#{onlineUser.getQueueName()}", containerFactory = "queueListener")
    public void readActiveQueue(Msg message) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        session.getBasicRemote().sendText(objectMapper.writeValueAsString(message));
    }

    public String getQueueName() {
        return this.queueName;
    }
}
