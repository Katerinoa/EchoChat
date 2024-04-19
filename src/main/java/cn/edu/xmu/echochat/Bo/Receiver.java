//package cn.edu.xmu.echochat.Bo;
//
//import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.stereotype.Component;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//
//@Component
//@ConditionalOnProperty(prefix = "spring.activemq.jms", name = "enable", havingValue = "true")
//public class Receiver {
//
//    @JmsListener(destination = "${spring.activemq.receiver-queue}", containerFactory = "queueListener")
//    public void readActiveQueue(Msg message) throws Exception {
//        System.out.println(String.format("\n收到私聊消息：%s", message.getContent().toString()));
//        if (message.getMessageType() == 1) {
//            try {
//                storeFile(message.getFileContent(), message.getFileType());
//                System.out.println("存储成功");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @JmsListener(destination = "${spring.activemq.topic-name}", containerFactory = "topicListener")
//    public void readActiveTopic(Msg message) throws Exception {
//        System.out.println(String.format("\n收到群发消息：%s", message.getContent().toString()));
//        if (message.getMessageType() == 1) {
//            try {
//                storeFile(message.getFileContent(), message.getFileType());
//                System.out.println("存储成功");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void storeFile(byte[] fileContent, String fileType) throws IOException {
//        String filePath = "src/main/resources/file" + "." + fileType;
//        try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
//            outputStream.write(fileContent);
//        }
//    }
//}
//
