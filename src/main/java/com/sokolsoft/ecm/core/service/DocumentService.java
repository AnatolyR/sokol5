package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.model.AuditRecord;
import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.specification.Specification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    DocumentServiceImpl.DocumentsPage getDocuments(Specification spec);

    Document getDocument(UUID documentId);

    Document save(Document document);

    void checkForDraft(Document document, List<String> roles);

    UUID createDocument(String documentType);

    void moveDocumentToState(UUID documentId, String title);

    Page<AuditRecord> getDocumentHistory(UUID documentId, Pageable pageable);
}
