package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.Utils;
import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.model.User;
import com.sokolsoft.ecm.core.repository.DocumentRepository;
import com.sokolsoft.ecm.core.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {
    private DocumentRepository documentRepository;

    private UserRepository userRepository;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository, UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
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

        fillTitles(oldDocument);

        return documentRepository.save(oldDocument);
    }

    private void fillTitles(Document document) {
        UUID addresseeId = document.getAddressee();
        if (addresseeId != null) {
            User addressee = userRepository.getOne(addresseeId);
            document.setAddresseeTitle(addressee.getTitle());
        }

        List<UUID> addresseeCopiesIds = document.getAddresseeCopies();
        List<String> addresseeCopiesTitles = new ArrayList<>();
        for (UUID addresseeCopyId : addresseeCopiesIds) {
            if (addresseeCopyId != null) {
                User addressee = userRepository.getOne(addresseeCopyId);
                addresseeCopiesTitles.add(addressee.getTitle());
            }
        }
        document.setAddresseeCopiesTitles(addresseeCopiesTitles);
    }

    @Override
    public UUID createDocument(String documentType) {
        Document document = new Document();
        UUID id = UUID.randomUUID();
        document.setId(id);
        document.setTitle("Новый документ");
        document.setStatus("Черновик");
        document.setDocumentType(documentType);
        document.setCreateDate(Instant.now());
        documentRepository.save(document);

        return id;
    }
}
