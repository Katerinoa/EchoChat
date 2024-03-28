package cn.edu.xmu.echochat.Bo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Msg {
    private Byte messageType; // 0-文本 1-文件
    private String content;
    private String fileType;
    private byte[] fileContent;
//    private LocalDateTime sentAt;
}
