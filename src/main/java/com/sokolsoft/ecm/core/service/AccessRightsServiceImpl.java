package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.model.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccessRightsServiceImpl implements AccessRightsService {
    private DocumentService documentService;

    private UserService userService;

    private SecurityService securityService;

    @Autowired
    public AccessRightsServiceImpl(DocumentService documentService,
                                   UserService userService,
                                   SecurityService securityService) {
        this.documentService = documentService;
        this.userService = userService;
        this.securityService = securityService;
    }

    @Override
    public List<String> getRolesForObject(UUID id, String objectType) {
        Set<String> roles = new HashSet<>();

        if ("document".equals(objectType)) {
            Document document = documentService.getDocument(id);
            UUID userId = userService.getCurrentUser().getId();
            roles.addAll(securityService.getCurrentUserRoles());

            if (userId.equals(document.getCreator())) {
                roles.add("ROLE_AUTHOR");
            }

            if (userId.equals(document.getAddressee())
                    || (document.getAddresseeCopies() != null && document.getAddresseeCopies().contains(userId))
                    || userId.equals(document.getExecutor())
                    || userId.equals(document.getController())) {
                roles.add("ROLE_INVOLVED");
                roles.add("ROLE_ATTACH_ADD");
                roles.add("ROLE_ATTACH_CONTENT");
            }

            if ("Черновик".equals(document.getStatus()) && roles.contains("ROLE_AUTHOR")) {
                roles.add("ROLE_ATTACH_ADD");
                roles.add("ROLE_ATTACH_DEL");
                roles.add("ROLE_ATTACH_CONTENT");
            }

            roles.add("ROLE_ATTACH_COUNT");
            roles.add("ROLE_ATTACH_LIST");
            
        }

        return new ArrayList<>(roles);
    }
}
