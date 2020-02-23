package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.Link;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LinkRepository extends JpaRepository<Link, UUID> {
    Page<Link> findByFromDocumentEquals(@Param("fromDocument") UUID objectId, Pageable p);

    long countByFromDocumentEquals(@Param("fromDocument") UUID from);

    long countByFromDocumentEqualsAndToDocumentEquals(@Param("fromDocument") UUID from, @Param("toDocument") UUID to);
}
