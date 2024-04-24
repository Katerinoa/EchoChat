package cn.edu.xmu.echochat.Mapper;

import cn.edu.xmu.echochat.Po.UserGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupUserPoMapper extends JpaRepository<UserGroupUser, Long> {
}
