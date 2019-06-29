package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.ContragentPerson;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RestResource(path = "contragentpersons")
@Secured("ROLE_SYSTEM")
public interface ContragentPersonRepository extends JpaRepository<ContragentPerson, UUID> {
    @RestResource(path = "personByTitle")
    @Secured({"ROLE_DIC_CONTRAGENT_PERSONS", "ROLE_SYSTEM"})
    Page findByOrganizationIdAndTitleLike(@Param("organizationId") UUID organizationId, @Param("title") String title, Pageable p);

    @Secured({"ROLE_DIC_CONTRAGENT_PERSONS", "ROLE_SYSTEM"})
    List<ContragentPerson> findByOrganizationIdAndTitle(@Param("organizationId") UUID organizationId, @Param("title") String title);
}
