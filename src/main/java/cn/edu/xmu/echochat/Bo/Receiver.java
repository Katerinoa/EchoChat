package cn.edu.xmu.echochat.Bo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "spring.activemq.jms", name = "enable", havingValue = "true")
public class Receiver {

    @JmsListener(destination = "${spring.activemq.queue-name}")
    public void readActiveQueue(String message) throws Exception {
        System.out.println(String.format("activeMq 使用 queue 模式接收到消息：%s", message));
    }

    @JmsListener(destination = "${spring.activemq.topic-name}", containerFactory = "topicListener")
    public void readActiveTopic1(String message) throws Exception {
        System.out.println(String.format("activeMq1 使用 topic 模式接收到消息：%s", message));
    }

}

