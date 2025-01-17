package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.DocumentKind;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RestResource
@Secured("ROLE_SYSTEM")
public interface DocumentKindRepository extends JpaRepository<DocumentKind, UUID> {
    @RestResource(path = "kindByTitle")
    @Secured({"ROLE_DIC_DOC_KINDS", "ROLE_SYSTEM"})
    Page<DocumentKind> findByTitleLike(@Param("title") String title, Pageable p);

    @Secured({"ROLE_DIC_DOC_KINDS_DEL", "ROLE_SYSTEM"})
    @Override
    void deleteAll(Iterable iterable);

    @Secured({"ROLE_DIC_DOC_KINDS_SAVE", "ROLE_SYSTEM"})
    @Override
    <S extends DocumentKind> S save(S s);

    @Secured({"ROLE_DIC_DOC_KINDS", "ROLE_SYSTEM"})
    @Override
    Page<DocumentKind> findAll(Pageable var1);
}
