package cn.edu.xmu.echochat.Bo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class File {

    private Long id;
    private String originalName;
    private String storagePath; // 文件在服务器存储的路径
    private Long ownerId; // 文件上传者的用户ID
    private Long size;
    private LocalDateTime uploadedAt;
}
