package com.sokolsoft.ecm.core.web;

import com.sokolsoft.ecm.core.Utils;
import com.sokolsoft.ecm.core.model.AuditRecord;
import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.service.DocumentService;
import com.sokolsoft.ecm.core.service.SecurityService;
import com.sokolsoft.ecm.core.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class DocumentController {
    private DocumentService documentService;

    private SecurityService securityService;

    private UserService userService;

    @Autowired
    public DocumentController(DocumentService documentService, SecurityService securityService,
                              UserService userService) {
        this.documentService = documentService;
        this.securityService = securityService;
        this.userService = userService;
    }

    @GetMapping("/api/document/{documentId}")
    public Object getDocument(@PathVariable("documentId") String documentIdStr, Authentication authentication) {
        UUID documentId = UUID.fromString(documentIdStr);
        Document document = documentService.getDocument(documentId);
        if (document == null) {
            throw new RuntimeException("Document not found");
        }
        DocumentResponse response = new DocumentResponse();
        response.setDocument(document);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<>(authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        documentService.checkForDraft(document, roles);

        Map<String, String> fieldsRights = securityService.getFieldsRights(document.getDocumentType(), document.getStatus(), roles);
        response.setFields(fieldsRights);

        response.setFieldsDescriptions(getFieldsDescriptions(document.getClass()));

        return response;
    }

    private List<FieldDescription> getFieldsDescriptions(Class documentClass) {
        return Utils.getFields(documentClass).stream()
                .filter(f -> f.getType().equals(Instant.class))
                .map(f -> FieldDescription.builder().name(f.getName()).type(f.getType().getSimpleName()).build())
                .collect(Collectors.toList());
    }

    @PostMapping("/api/document")
    public Document save(@RequestBody Document document) {
        return documentService.save(document);
    }

    @PostMapping("/api/createDocument/{documentType}")
    public String createDocument(@PathVariable("documentType") String documentType) {
        UUID documentId = documentService.createDocument(documentType);
        return documentId.toString();
    }

    @GetMapping("/api/document/{documentId}/history")
    public Page<AuditRecord> getDocumentHistory(@PathVariable("documentId") UUID documentId, Pageable pageable) {
        return documentService.getDocumentHistory(documentId, pageable);
    }

    @Data
    public static class DocumentResponse {
        private Document document;
        private Map<String, String> fields;
        private List<FieldDescription> fieldsDescriptions;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FieldDescription {
        private String name;
        private String type;
    }
}
