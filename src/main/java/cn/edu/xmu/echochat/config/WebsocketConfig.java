package cn.edu.xmu.echochat.config;

import cn.edu.xmu.echochat.Mapper.UserGroupPoMapper;
import cn.edu.xmu.echochat.Mapper.UserGroupUserPoMapper;
import cn.edu.xmu.echochat.Mapper.UserPoMapper;
import cn.edu.xmu.echochat.ws.WebsocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebsocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }

    @Autowired
    public void setUserPoMapper(UserPoMapper userPoMapper, UserGroupPoMapper userGroupPoMapper, UserGroupUserPoMapper userGroupUserPoMapper) {
        WebsocketServer.userPoMapper = userPoMapper;
        WebsocketServer.userGroupPoMapper = userGroupPoMapper;
    }
}