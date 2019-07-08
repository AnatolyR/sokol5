package com.sokolsoft.ecm.core.web;

import com.sokolsoft.ecm.core.model.*;
import com.sokolsoft.ecm.core.service.DocumentService;
import com.sokolsoft.ecm.core.service.SecurityService;
import com.sokolsoft.ecm.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

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

        return response;
    }

//    @PostMapping("document")
//    public Document save(@RequestBody Document document) {
//        return documentService.save(document);
//    }

    @PostMapping("/api/incomingDocument")
    public Document save(@RequestBody IncomingDocument document) {
        return documentService.save(document);
    }

    @PostMapping("/api/outgoingDocument")
    public Document save(@RequestBody OutgoingDocument document) {
        return documentService.save(document);
    }

    @PostMapping("/api/innerDocument")
    public Document save(@RequestBody InnerDocument document) {
        return documentService.save(document);
    }

    @PostMapping("/api/createDocument/{documentType}")
    public String createDocument(@PathVariable("documentType") String documentType) {
        UUID documentId = documentService.createDocument(documentType);
        return documentId.toString();
    }

    public static class DocumentResponse {
        private Document document;
        private Map<String, String> fields;

        public Document getDocument() {
            return document;
        }

        public void setDocument(Document document) {
            this.document = document;
        }

        public Map<String, String> getFields() {
            return fields;
        }

        public void setFields(Map<String, String> fields) {
            this.fields = fields;
        }
    }
}
