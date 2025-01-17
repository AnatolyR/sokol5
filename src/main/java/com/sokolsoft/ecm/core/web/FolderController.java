package com.sokolsoft.ecm.core.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sokolsoft.ecm.core.model.IncomingDocument;
import com.sokolsoft.ecm.core.model.InnerDocument;
import com.sokolsoft.ecm.core.model.OutgoingDocument;
import com.sokolsoft.ecm.core.model.Task;
import com.sokolsoft.ecm.core.service.ConfigService;
import com.sokolsoft.ecm.core.service.DocumentService;
import com.sokolsoft.ecm.core.service.DocumentServiceImpl;
import com.sokolsoft.ecm.core.service.UserService;
import com.sokolsoft.ecm.core.specification.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class FolderController {
    private static final String DEFAULT_PAGE_SIZE = "10";
    private final DocumentService documentService;

    private final ConfigService configService;

    private final ObjectMapper mapper;

    private final UserService userService;

    @GetMapping(value = "/api/folders/{folderId}/data", produces = "application/json")
    public Object getData(@PathVariable String folderId,
                          @RequestParam(defaultValue = DEFAULT_PAGE_SIZE) Integer size,
                          @RequestParam(defaultValue = "0") Integer page,
                          String sortDirection,
                          String sortProperty,
                          @RequestParam(defaultValue = "") String conditions,
                          Authentication authentication) {
        
        Specification spec = new Specification();
        if (sortProperty != null && !sortProperty.isEmpty()) {
            Sort sortObject = new Sort();
            sortObject.setField(sortProperty);
            sortObject.setOrder(SortOrder.valueOf(sortDirection));
            spec.setSort(Collections.singletonList(sortObject));
        }

        if (folderId == null || folderId.split("/").length > 2 || !folderId.replace("/", "").matches("^[a-zA-Z_]+$")) {
            throw new RuntimeException("Empty or wrong folderId");
        }
        JsonNode config = configService.getPrivateConfig("folders/" + folderId);

        checkAccess(config, authentication);

        if (config.has("documentClass")) {
            String documentClassStr = config.get("documentClass").asText();
            switch (documentClassStr) {
                case "IncomingDocument": spec.setDocumentClass(IncomingDocument.class); break;
                case "OutgoindDocument": spec.setDocumentClass(OutgoingDocument.class); break;
                case "InnerDocument": spec.setDocumentClass(InnerDocument.class); break;
                case "Task":
                    spec.setDocumentClass(Task.class);
                    spec.setJoin("document");
                    break;
            }
        }

        try {
            JsonNode clientConditionsNode = mapper.readTree("[" + conditions + "]");
            Condition clientCondition = SpecificationUtil.read((ArrayNode) clientConditionsNode);

            JsonNode configConditionsNode = config.get("condition");
            configConditionsNode.forEach(c -> {
                if (c.has("template")) {
                    String template = c.get("template").asText();
                    if (template.equals("currentUserId")) {
                        ((ObjectNode) c).put("value", userService.getCurrentUser().getId().toString());
                    }
                }
            });
            Condition configCondition = SpecificationUtil.read((ArrayNode) configConditionsNode);

            Condition andCondition = new ContainerCondition(ContainerOperation.AND, clientCondition, configCondition);

            spec.setCondition(andCondition);
        } catch (IOException e) {
            e.printStackTrace();
        }

        spec.setPage(page);
        spec.setSize(size);

        DocumentServiceImpl.DocumentsPage documentsPage = documentService.getDocuments(spec);

        return documentsPage;
    }
    
    private void checkAccess(JsonNode config, Authentication authentication) {
        String secured = config.get("secured").asText();

        ExpressionParser parser = new SpelExpressionParser();

        Expression exp = parser.parseExpression(secured);
        Boolean result = (Boolean) exp.getValue(new Object() {
            public boolean hasRole(String role) {
                return authentication.getAuthorities().stream().anyMatch(a -> ((GrantedAuthority) a).getAuthority().equals(role));
            }
        });
        if (!result) {
            throw new RuntimeException("There is no access rights to this folder");
        }
    }
}
