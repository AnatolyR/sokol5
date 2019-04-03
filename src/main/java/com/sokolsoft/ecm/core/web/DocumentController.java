package com.sokolsoft.ecm.core.web;

import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class DocumentController {
    private DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("document/{documentId}")
    public Object getDocument(@PathVariable("documentId") String documentIdStr) {
        UUID documentId = UUID.fromString(documentIdStr);
        return documentService.getDocument(documentId);
    }

    @PostMapping("document")
    public Document save(@RequestBody Document document) {
        return documentService.save(document);
    }

    @PostMapping("createDocument/{documentType}")
    public String createDocument(@PathVariable("documentType") String documentType) {
        UUID documentId = documentService.createDocument(documentType);
        return documentId.toString();
    }
}
