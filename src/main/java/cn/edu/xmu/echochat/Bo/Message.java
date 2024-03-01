package cn.edu.xmu.echochat.Bo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Message {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private LocalDateTime sentAt;
    private String messageType; // 如文本、图片、文件等
}
