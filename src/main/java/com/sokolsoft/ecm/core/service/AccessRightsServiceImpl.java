package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.SokolException;
import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.repository.DocumentRepository;
import com.sokolsoft.ecm.core.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccessRightsServiceImpl implements AccessRightsService {
    private final DocumentService documentService;

    private final UserService userService;

    private final SecurityService securityService;

    private final DocumentRepository documentRepository;

    private final TaskRepository taskRepository;

    @Override
    public List<String> getRolesForObject(UUID id, String objectType) {
        Set<String> roles = new HashSet<>();

        if ("document".equals(objectType)) {
            Document document = documentRepository.findById(id).orElseThrow(() -> new SokolException("004", "Документ не найден"));
            UUID userId = userService.getCurrentUser().getId();
            roles.addAll(securityService.getCurrentUserRoles());

            if (userId.equals(document.getCreator())) {
                roles.add("ROLE_AUTHOR");
            }

            if (isUserExecutor(userId, document.getId())) {
                roles.add("ROLE_EXECUTOR");
            }

            if (isUserController(userId, document.getId())) {
                roles.add("ROLE_CONTROLLER");
            }

            if (userId.equals(document.getAddressee())
                    || (document.getAddresseeCopies() != null && document.getAddresseeCopies().contains(userId))
                    || userId.equals(document.getExecutor())
                    || userId.equals(document.getController())) {
                roles.add("ROLE_INVOLVED");
                roles.add("ROLE_ATTACH_ADD");
                roles.add("ROLE_ATTACH_CONTENT");

                roles.add("ROLE_LINK_ADD");
                roles.add("ROLE_LINK_CONTENT");
            }

            if ("Черновик".equals(document.getStatus()) && roles.contains("ROLE_AUTHOR")) {
                roles.add("ROLE_ATTACH_ADD");
                roles.add("ROLE_ATTACH_DEL");
                roles.add("ROLE_ATTACH_CONTENT");

                roles.add("ROLE_LINK_ADD");
                roles.add("ROLE_LINK_DEL");
                roles.add("ROLE_LINK_CONTENT");
            }

            roles.add("ROLE_ATTACH_COUNT");
            roles.add("ROLE_ATTACH_LIST");

            roles.add("ROLE_LINK_COUNT");
            roles.add("ROLE_LINK_LIST");

        }

        return new ArrayList<>(roles);
    }

    private boolean isUserController(UUID userId, UUID documentId) {
        return documentRepository.getOne(documentId).getStatus().equals("На исполнении")
                && taskRepository.findAllByDocumentId(documentId).stream()
                .anyMatch(t -> t.getType().equals("execution")
                        && t.getControllerId().equals(userId)
                        && t.getStatus().equals("control"));
    }

    private boolean isUserExecutor(UUID userId, UUID documentId) {
        return documentRepository.getOne(documentId).getStatus().equals("На исполнении")
                && taskRepository.findAllByDocumentId(documentId).stream()
                .anyMatch(t -> t.getType().equals("execution")
                        && t.getExecutorId().equals(userId)
                        && t.getStatus().equals("execution"));
    }

    @Override
    public boolean isActionAvailable(UUID objectId, String objectType, String action) {
        return getActionsForObject(objectId, objectType).contains(action);
    }

    @Override
    public List<String> getActionsForObject(UUID objectId, String objectType) {
        List<String> rolesForObject = getRolesForObject(objectId, objectType);

        if (objectType.equals("document")) {
            Document document = documentRepository.findById(objectId).orElseThrow(() -> new SokolException("004", "Документ не найден"));
            List<String> actions = securityService.getFieldsRights(document.getDocumentType(), document.getStatus(), rolesForObject)
                    .entrySet().stream()
                    .filter(e -> e.getKey().startsWith("@"))
                    .filter(e -> e.getValue().equals("1"))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            return actions;
        }
        return Collections.emptyList();
    }
}
