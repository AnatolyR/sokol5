package com.sokolsoft.ecm.core.web;

import com.sokolsoft.ecm.core.SokolException;
import com.sokolsoft.ecm.core.SokolSecurityException;
import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.model.Link;
import com.sokolsoft.ecm.core.repository.LinkRepository;
import com.sokolsoft.ecm.core.service.AccessRightsService;
import com.sokolsoft.ecm.core.service.DocumentService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@Data
@RequiredArgsConstructor
public class LinksController {
    private final AccessRightsService accessRightsService;

    private final LinkRepository linkRepository;

    private final DocumentService documentService;

    @GetMapping(path = "/api/links/search/countByObjectId")
    @ResponseBody
    public long countByObjectId(@Param("objectId") String objectId) {
        List<String> rolesForObject = accessRightsService.getRolesForObject(UUID.fromString(objectId), "document");
        if (rolesForObject.contains("ROLE_LINK_COUNT")) {
            return linkRepository.countByFromDocumentEquals(UUID.fromString(objectId));
        } else {
            return 0;
        }
    }

    @GetMapping(path = "/api/links/search/linksByObjectId")
    @ResponseBody
    public Page getLinks(@Param("objectId") String objectId, Pageable p) {
        List<String> rolesForObject = accessRightsService.getRolesForObject(UUID.fromString(objectId), "document");
        if (rolesForObject.contains("ROLE_LINK_LIST")) {
            return linkRepository.findByFromDocumentEquals(UUID.fromString(objectId), p)
                    .map(link -> {
                        Document document = null;
                        try {
                            document = documentService.getDocument(link.getToDocument());
                        } catch (RuntimeException e) {
                            //
                        }
                        return LinkWithDetails.builder()
                                .link(link)
                                .document(document)
                                .build();
                    });
        } else {
            return Page.empty();
        }

    }

    @PostMapping(path = "/api/links/search/linksByObjectId")
    public void addLink(@RequestBody Link link) {
        //todo check AR for toDocument
        if (linkRepository.countByFromDocumentEqualsAndToDocumentEquals(link.getFromDocument(), link.getToDocument()) != 0) {
            throw new SokolException("012", "Документы уже связаны");
        }
        if (documentService.getDocument(link.getFromDocument()) == null) {
            throw new SokolException("013", "Документ для связи не существует");
        }
        if (documentService.getDocument(link.getToDocument()) == null) {
            throw new SokolException("014", "Документ для связи не существует");
        }
        List<String> rolesForObject = accessRightsService.getRolesForObject(UUID.fromString(link.getFromDocument().toString()), "document");
        if (rolesForObject.contains("ROLE_LINK_ADD")) {
            linkRepository.save(link);
        } else {
            throw new SokolSecurityException("ROLE_LINK_ADD", "Нет прав на связывание документа");
        }
    }


    @GetMapping(path = "/api/links/availableActions")
    @ResponseBody
    public List<String> getAvailableActions(@Param("objectId") String objectId, @Param("objectType") String objectType) {
        List<String> actions = accessRightsService.getActionsForObject(UUID.fromString(objectId), objectType);
        List<String> resultActions = new ArrayList<>();
        if (actions.contains("@addLink")) {
            resultActions.add("add");
        }
        if (actions.contains("@deleteLink")) {
            resultActions.add("del");
        }
        if (actions.contains("@viewLink")) {
            resultActions.add("view");
        }
        return resultActions;
    }

    @DeleteMapping(value = "/api/delete/links/{ids}")
    @Transactional
    public void deleteLinks(@PathVariable String ids) {
        String[] split = ids.split(",");
        List<Link> links = linkRepository.findAllById(Arrays.stream(split)
                .map(UUID::fromString)
                .collect(Collectors.toList()));
        if (links.size() != split.length) {
            throw new RuntimeException("Cannot find links to delete");
        }

        links.forEach(link -> {
            List<String> rolesForObject = accessRightsService.getRolesForObject(link.getFromDocument(), "document");
            if (!rolesForObject.contains("ROLE_LINK_DEL")) {
                throw new RuntimeException("No access rights to delete link");
            }
        });

        linkRepository.deleteAll(links);
    }


    @Data
    @Builder
    public static class LinkWithDetails {
        private Link link;

        private Document document;
    }
}
