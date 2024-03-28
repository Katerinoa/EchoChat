package cn.edu.xmu.echochat.Bo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;

@Component
@ConditionalOnProperty(prefix = "spring.activemq.jms", name = "enable", havingValue = "true")
public class Receiver {

    @JmsListener(destination = "${spring.activemq.receiever-queue}", containerFactory = "queueListener")
    public void readActiveQueue(Msg message) throws Exception {
        System.out.println(String.format("activeMq 使用 queue 模式接收到消息：%s", message.toString()));
        if (message.getMessageType() == 1) {
            try {
                storeFile(message.getFileContent(), message.getFileType());
                System.out.println("存储成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @JmsListener(destination = "${spring.activemq.topic-name}", containerFactory = "topicListener")
    public void readActiveTopic(Msg message) throws Exception {
        System.out.println(String.format("activeMq 使用 topic 模式接收到消息：%s", message.toString()));
        if (message.getMessageType() == 1) {
            try {
                storeFile(message.getFileContent(), message.getFileType());
                System.out.println("存储成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void storeFile(byte[] fileContent, String fileType) throws IOException {
        String filePath = "src/main/resources/file" + "." + fileType;
        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
            outputStream.write(fileContent);
        }
    }
}

