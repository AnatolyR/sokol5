package com.sokolsoft.ecm.core.web;

import com.sokolsoft.ecm.core.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FolderController {
    private DocumentService documentService;

    @Autowired
    public FolderController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping(value = "/folders/{folderId}/data", produces = "application/json")
    public Object getData(@PathVariable String folderId) {

        return documentService.getDocuments();
    }
}
