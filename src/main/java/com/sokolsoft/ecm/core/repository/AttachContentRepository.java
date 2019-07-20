package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.Attach;
import com.sokolsoft.ecm.core.model.AttachContent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AttachContentRepository extends JpaRepository<AttachContent, UUID> {
}
