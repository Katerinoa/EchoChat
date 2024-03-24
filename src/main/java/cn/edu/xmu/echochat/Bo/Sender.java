package cn.edu.xmu.echochat.Bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import jakarta.jms.Destination;
import jakarta.jms.Queue;
import jakarta.jms.Topic;

@RestController
@ConditionalOnProperty(prefix = "spring.activemq.jms", name = "enable", havingValue = "true")
public class Sender {

    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Queue queue;

    @Autowired
    private Topic topic;

    public void sendQueue(String message) {
        System.out.println(String.format("activeMq 使用 queue 模式发送消息：%s", message));
        sendMessage(queue, message);
    }

    public void sendTopic(String message) {
        System.out.println(String.format("activeMq 使用 topic 模式发送消息：%s", message));
        sendMessage(topic, message);
    }

    // 发送消息，destination是发送到的队列，message是待发送的消息
    private void sendMessage(Destination destination, String message) {
        jmsMessagingTemplate.convertAndSend(destination, message);
    }

}

