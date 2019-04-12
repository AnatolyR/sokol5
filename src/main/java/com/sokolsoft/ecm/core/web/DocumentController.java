package com.sokolsoft.ecm.core.web;

import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.model.User;
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

    @GetMapping("document/{documentId}")
    public Object getDocument(@PathVariable("documentId") String documentIdStr, Authentication authentication) {
        UUID documentId = UUID.fromString(documentIdStr);
        Document document = documentService.getDocument(documentId);
        DocumentResponse response = new DocumentResponse();
        response.setDocument(document);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        List<String> roles = new ArrayList<>(authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

        User currentUser = userService.getCurrentUser();
        UUID creator = document.getCreator();
        if (currentUser.getId().equals(creator)) {
            roles.add("ROLE_AUTHOR");
        }
        String status = document.getStatus();
        if ("Черновик".equals(status) && !roles.contains("ROLE_AUTHOR")) {
            throw new RuntimeException("Document in draft state available only to author");
        }

        Map<String, String> fieldsRights = securityService.getFieldsRights(document.getDocumentType(), document.getStatus(), roles);
        response.setFields(fieldsRights);

        return response;
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
