package cn.edu.xmu.echochat.ws;

import cn.edu.xmu.echochat.Bo.User;
import cn.edu.xmu.echochat.Mapper.UserPoMapper;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@ServerEndpoint("/chat/{username}/{password}")
@Component
public class WebsocketServer {

    private Session session;
    private String username;
    private String password;

    public static UserPoMapper userPoMapper;

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username, @PathParam("password") String password) throws IOException {
        log.info("session open: " + username + "  " + password);
        this.username = username;
        this.password = password;
        this.session = session;

        User user = this.userPoMapper.findByUsernameAndPassword(username, password);
        if (user != null) {
            log.info("login success: " + this.username);
        } else {
            log.info("failed matching: " + this.username);
            session.close();
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info(message);
    }

    @OnClose
    public void onClose(Session session) {

    }
}
