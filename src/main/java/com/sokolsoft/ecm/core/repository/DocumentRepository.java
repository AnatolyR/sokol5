package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.Document;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

}
