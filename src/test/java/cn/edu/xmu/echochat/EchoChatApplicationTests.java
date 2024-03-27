package cn.edu.xmu.echochat;

import cn.edu.xmu.echochat.Bo.Msg;
import cn.edu.xmu.echochat.Bo.Sender;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@SpringBootTest
class EchoChatApplicationTests {

    @Resource
    private Sender sender;

    @Test
    void test() throws IOException {
        Msg message = new Msg();
        message.setId(0L);
        message.setSenderId(0L);
        message.setReceiverId(0L);
        message.setMessageType((byte) 0);
        message.setContent("123");
        sender.sendQueue(message);

        String filePath = "src/main/resources/test.txt";
        Msg messageFile = new Msg();
        messageFile.setId(0L);
        messageFile.setSenderId(0L);
        messageFile.setReceiverId(0L);
        messageFile.setMessageType((byte) 1);
        messageFile.setFileContent(Files.readAllBytes(Path.of(filePath)));
        sender.sendQueue(messageFile);

        sender.sendTopic(message);
    }
}
