package cn.edu.xmu.echochat.Mapper;

import cn.edu.xmu.echochat.Po.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupPoMapper extends JpaRepository<UserGroup, Long> {
    UserGroup findByName(String name);

    @Query(value = "select distinct a from UserGroup a join UserGroupUser b on a.id = b.groupId " +
            "where b.userId = :userId")
    List<UserGroup> findByUserIdEquals(Long userId);

}
