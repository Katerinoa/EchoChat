package cn.edu.xmu.echochat;

import java.util.Scanner;

import cn.edu.xmu.echochat.Bo.Msg;
import cn.edu.xmu.echochat.Bo.Sender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ChatCommandLineRunner implements CommandLineRunner {

    private final Sender sender;

    // 构造器注入Sender
    public ChatCommandLineRunner(Sender sender) {
        this.sender = sender;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println("请输入消息类型（0-文本，1-文件）:");
                byte messageType = scanner.nextByte();
                scanner.nextLine(); // 清除换行符

                Msg msg = new Msg();
                msg.setMessageType(messageType);

                if (messageType == 0) {
                    System.out.println("请输入消息内容:");
                    String content = scanner.nextLine();
                    msg.setContent(content);
                } else {
                    // 对于文件类型的消息，可以在这里添加逻辑处理
                    System.out.println("文件消息暂不支持通过命令行发送。");
                }

                sender.sendQueue(msg); // 或者 sender.sendTopic(msg); 根据实际情况调用
            }
        }
    }
}
