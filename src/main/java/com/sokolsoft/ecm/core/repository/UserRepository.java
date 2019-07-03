package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RepositoryRestResource
@Secured("ROLE_SYSTEM")
public interface UserRepository extends JpaRepository<User, UUID> {

    @RestResource(path = "userByTitle")
    @Secured({"ROLE_DIC_USERS", "ROLE_SYSTEM"})
    public Page findByTitleLike(@Param("title") String title, Pageable p);
//    public Page findByTitleStartsWith(@Param("title") String title, Pageable p);

    @Secured({"ROLE_USER", "ROLE_SYSTEM"})
    @Override
    User getOne(UUID uuid);

    @Secured({"ROLE_DIC_USERS", "ROLE_SYSTEM"})
    @Override
    Page<User> findAll(Pageable var1);

    @Secured({"ROLE_DIC_USERS", "ROLE_SYSTEM"})
    @Override
    Optional<User> findById(UUID uuid);

    @Secured({"ROLE_DIC_USERS_SAVE", "ROLE_SYSTEM"})
    @Override
    <S extends User> S save(S s);
}
