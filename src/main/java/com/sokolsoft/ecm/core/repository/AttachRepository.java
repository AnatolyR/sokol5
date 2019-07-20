package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.Attach;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AttachRepository extends JpaRepository<Attach, UUID> {
    Page findByObjectIdEqualsAndObjectTypeEquals(@Param("objectId") UUID objectId,
                                                 @Param("objectType") String objectType,
                                                 Pageable p);

    long countByObjectIdEqualsAndObjectTypeEquals(@Param("objectId") UUID objectId, @Param("objectType") String objectType);
}
