package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.DeliveryMethod;
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
public interface DeliveryMethodRepository extends JpaRepository<DeliveryMethod, UUID> {
    @RestResource(path = "methodByTitle")
    @Secured({"ROLE_DIC_DELIVERY_METHODS", "ROLE_SYSTEM"})
    public Page findByTitleLike(@Param("title") String title, Pageable p);

    @Secured({"ROLE_DIC_DELIVERY_METHODS_DEL", "ROLE_SYSTEM"})
    @Override
    void deleteAll(Iterable iterable);

    @Secured({"ROLE_DIC_DELIVERY_METHODS_SAVE", "ROLE_SYSTEM"})
    @Override
    <S extends DeliveryMethod> S save(S s);

    @Secured({"ROLE_DIC_DELIVERY_METHODS", "ROLE_SYSTEM"})
    @Override
    Page<DeliveryMethod> findAll(Pageable var1);
}
