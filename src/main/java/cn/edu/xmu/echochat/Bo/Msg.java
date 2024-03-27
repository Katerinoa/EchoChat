package cn.edu.xmu.echochat.Bo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Msg {
    private Long id;
    private Long senderId;
    private Long receiverId;
    private Byte messageType; // 0-文本 1-txt
    private String content;
    private byte[] fileContent;
//    private LocalDateTime sentAt;
}
