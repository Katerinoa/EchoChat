package cn.edu.xmu.echochat;

import cn.edu.xmu.echochat.Bo.Sender;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class EchoChatApplicationTests {

    @Resource
    private Sender sender;

    @Test
    void test() {
//        System.out.println("1");
        sender.sendQueue("测试queue");
        sender.sendTopic("测试topic");
    }

}
