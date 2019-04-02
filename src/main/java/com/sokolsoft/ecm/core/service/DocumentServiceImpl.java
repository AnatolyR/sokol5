package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.Utils;
import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.repository.DocumentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {
    private DocumentRepository documentRepository;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    @Override
    public List<Document> getDocuments() {
        return documentRepository.findAll();
    }

    @Override
    public Document getDocument(UUID documentId) {
        return documentRepository.findById(documentId).orElse(null);
    }

    @Override
    public Document save(Document document) {
        Document oldDocument = documentRepository.getOne(document.getId());
        BeanUtils.copyProperties(document, oldDocument, Utils.getNullPropertyNames(document));
        return documentRepository.save(oldDocument);
    }
}
