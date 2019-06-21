package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.specification.Specification;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface DocumentService {
    Page<Document> getDocuments(Specification spec);

    Document getDocument(UUID documentId);


    Document save(Document document);

    UUID createDocument(String documentType);
}
