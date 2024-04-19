package cn.edu.xmu.echochat.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;
import jakarta.jms.Topic;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "spring.activemq.jms", name = "enable", havingValue = "true")
public class ActiveMQConfig {

    private static final Map<String, Queue> queues = new HashMap<>();
    private static final Map<String, Topic> topics = new HashMap<>();

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String userName;

    @Value("${spring.activemq.password}")
    private String password;

    public static Queue queue(String queueName) {
        if (queues.containsKey(queueName)) {
            log.info("reusing existing queue: " + queueName);
            return queues.get(queueName);
        } else {
            log.info("creating new queue: " + queueName);
            Queue queue = new ActiveMQQueue(queueName);
            queues.put(queueName, queue);
            return queue;
        }
    }

    public static Topic topic(String topicName) {
        if (topics.containsKey(topicName)) {
            log.info("Reusing existing topic: " + topicName);
            return topics.get(topicName);
        } else {
            log.info("Creating new topic: " + topicName);
            Topic topic = new ActiveMQTopic(topicName);
            topics.put(topicName, topic);
            return topic;
        }
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(userName, password, brokerUrl);
    }

    @Bean
    public MessageConverter customMessageConverter() {
        return new CustomMessageConverter();
    }

    @Bean("queueListener")
    public JmsListenerContainerFactory<?> queueJmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(customMessageConverter());
        factory.setPubSubDomain(false);
        return factory;
    }

    @Bean("topicListener")
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(customMessageConverter());
        factory.setPubSubDomain(true);
        return factory;
    }
}

