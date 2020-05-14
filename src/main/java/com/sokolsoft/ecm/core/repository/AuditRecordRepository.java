package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.AuditRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

public interface AuditRecordRepository extends JpaRepository<AuditRecord, UUID> {

    @Transactional
    Page<AuditRecord> findAllByObjectId(UUID objectId, Pageable pageable);

}
