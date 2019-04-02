package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.model.Document;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    List<Document> getDocuments();

    Document getDocument(UUID documentId);


    Document save(Document document);
}
