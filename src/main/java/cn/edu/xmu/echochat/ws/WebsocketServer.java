package cn.edu.xmu.echochat.ws;

import cn.edu.xmu.echochat.Bo.ApplicationContextProvider;
import cn.edu.xmu.echochat.Bo.Msg;
import cn.edu.xmu.echochat.Bo.OnlineUser;
import cn.edu.xmu.echochat.Bo.User;
import cn.edu.xmu.echochat.Mapper.UserPoMapper;
import cn.edu.xmu.echochat.config.SpringContextUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@ServerEndpoint("/chat/{username}/{password}")
@Component
public class WebsocketServer {
    private JmsMessagingTemplate jmsMessagingTemplate;
    private ApplicationContext context;

    private Session session;
    private String username;
    private String password;
    private OnlineUser onlineUser;

    public static UserPoMapper userPoMapper;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username, @PathParam("password") String password) throws IOException {
        log.info("session open: " + username + "  " + password);
        this.username = username;
        this.password = password;
        this.session = session;
        this.jmsMessagingTemplate = SpringContextUtil.getBean(JmsMessagingTemplate.class);
        context = ApplicationContextProvider.getContext();

        User user = this.userPoMapper.findByUsernameAndPassword(username, password);
        if (user != null) {
            log.info("login success: " + this.username);
            this.onlineUser = new OnlineUser(session, user.getId(), jmsMessagingTemplate, (JmsListenerContainerFactory<?>) context.getBean("queueListener"));
        } else {
            log.info("failed matching: " + this.username);
            session.close();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) throws JsonProcessingException {
        log.info(message);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        Msg msg = objectMapper.readValue(message, Msg.class);
        User receiver = this.userPoMapper.findByUsername(msg.getReceiver());
        if (msg.getReceiverType() == 0) {
            this.onlineUser.sendQueue(receiver.getId(), msg);
        }
    }

    @OnClose
    public void onClose(Session session) {

    }
}
