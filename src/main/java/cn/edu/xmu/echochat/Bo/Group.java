package cn.edu.xmu.echochat.Bo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Group {

    private Long id;
    private String name;
    private Long ownerId; // 群组创建者的用户ID
    private List<Long> memberIds; // 群组成员的用户ID列表
}
