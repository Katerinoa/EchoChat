package cn.edu.xmu.echochat.Po;

import jakarta.persistence.*;

@Entity
@Table(name = "user_group_user")
public class UserGroupUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long groupId;
}
