package cn.edu.xmu.echochat.ws;

import cn.edu.xmu.echochat.Bo.ApplicationContextProvider;
import cn.edu.xmu.echochat.Bo.OnlineUser;
import cn.edu.xmu.echochat.Mapper.UserGroupPoMapper;
import cn.edu.xmu.echochat.Mapper.UserPoMapper;
import cn.edu.xmu.echochat.Po.Msg;
import cn.edu.xmu.echochat.Po.User;
import cn.edu.xmu.echochat.Po.UserGroup;
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
import org.springframework.context.ApplicationContext;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@ServerEndpoint("/chat/{username}/{password}")
@Component
public class WebsocketServer {
    private JmsMessagingTemplate jmsMessagingTemplate;
    private ApplicationContext context;

    private Session session;
    private OnlineUser onlineUser;

    public static UserPoMapper userPoMapper;
    public static UserGroupPoMapper userGroupPoMapper;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username, @PathParam("password") String password) throws IOException {
        log.info("session open: " + username + "  " + password);
        this.session = session;
        this.jmsMessagingTemplate = SpringContextUtil.getBean(JmsMessagingTemplate.class);
        context = ApplicationContextProvider.getContext();

        User user = userPoMapper.findByUsernameAndPassword(username, password);
        List<UserGroup> userGroupList = userGroupPoMapper.findByUserIdEquals(user.getId());

        if (user != null) {
            log.info("login success: " + username);
            this.onlineUser = new OnlineUser(session, user.getId(), jmsMessagingTemplate, (JmsListenerContainerFactory<?>) context.getBean("queueListener"), (JmsListenerContainerFactory<?>) context.getBean("topicListener"));
        } else {
            log.info("failed matching: " + username);
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
        if (msg.getReceiverType() == 0) {
            // 发送私聊消息
            User receiver = userPoMapper.findByUsername(msg.getReceiver());
            this.onlineUser.sendQueue(receiver.getId(), msg);
        } else if (msg.getReceiverType() == 1) {
            // 发送群聊消息
            UserGroup userGroup = userGroupPoMapper.findByName(msg.getReceiver());
            this.onlineUser.sendTopic(userGroup.getId(), msg);
        }
    }

    @OnClose
    public void onClose(Session session) {

    }
}
