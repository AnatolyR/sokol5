package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RestResource
public interface UserRepository extends JpaRepository<User, UUID> {

    @RestResource(path = "userByTitle")
    public Page findByTitleLike(@Param("title") String title, Pageable p);
//    public Page findByTitleStartsWith(@Param("title") String title, Pageable p);
}
