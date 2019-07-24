package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
    @Query("select g from Group g join g.member m where m = :member")
    List<Group> findAllByMemberContaining(@Param("member") String member);
}
