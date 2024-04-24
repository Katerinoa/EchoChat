package cn.edu.xmu.echochat.Mapper;

import cn.edu.xmu.echochat.Po.Msg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MsgPoMapper extends JpaRepository<Msg, Long> {
}
