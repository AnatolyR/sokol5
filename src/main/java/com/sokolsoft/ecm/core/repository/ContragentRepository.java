package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.Contragent;
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
public interface ContragentRepository extends JpaRepository<Contragent, UUID> {
    @RestResource(path = "userByTitle")
    @Secured({"ROLE_DIC_CONTRAGENTS", "ROLE_SYSTEM"})
    public Page findByTitleLike(@Param("title") String title, Pageable p);

    @Secured({"ROLE_USER", "ROLE_SYSTEM"})
    @Override
    Contragent getOne(UUID uuid);

    @Secured({"ROLE_DIC_CONTRAGENTS", "ROLE_SYSTEM"})
    @Override
    Page<Contragent> findAll(Pageable var1);

    @Secured({"ROLE_DIC_CONTRAGENTS", "ROLE_SYSTEM"})
    @Override
    Optional<Contragent> findById(UUID uuid);

    @Secured({"ROLE_DIC_CONTRAGENTS_SAVE", "ROLE_SYSTEM"})
    @Override
    <S extends Contragent> S save(S s);

    @Secured({"ROLE_DIC_CONTRAGENTS_DEL", "ROLE_SYSTEM"})
    @Override
    void deleteAll(Iterable iterable);
}
