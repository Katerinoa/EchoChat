package cn.edu.xmu.echochat.config;

import org.apache.activemq.ActiveMQConnectionFactory;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;

import jakarta.jms.ConnectionFactory;
import jakarta.jms.Queue;
import jakarta.jms.Topic;

@Configuration
@ConditionalOnProperty(prefix = "spring.activemq.jms", name = "enable", havingValue = "true")
public class ActiveMQConfig {

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;

    @Value("${spring.activemq.user}")
    private String userName;

    @Value("${spring.activemq.password}")
    private String password;

    @Value("${spring.activemq.queue-name}")
    private String queueName;

    @Value("${spring.activemq.topic-name}")
    private String topicName;


    @Bean
    public Queue queue() {
        System.out.println("注入队列实例：" + queueName);
        return new ActiveMQQueue(queueName);
    }

    @Bean
    public Topic topic() {
        System.out.println("注入广播实例：" + topicName);
        return new ActiveMQTopic(topicName);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory(userName, password, brokerUrl);
    }

    @Bean("queueListener")
    public JmsListenerContainerFactory<?> queueJmsListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(false);
        return factory;
    }

    @Bean("topicListener")
    public JmsListenerContainerFactory<?> jmsListenerContainerTopic(ConnectionFactory connectionFactory) {
        DefaultJmsListenerContainerFactory bean = new DefaultJmsListenerContainerFactory();
        bean.setConnectionFactory(connectionFactory);
        //设置广播模式，这样才能接收到topic 发送的消息
        bean.setPubSubDomain(true);
        return bean;
    }

}

