package com.sokolsoft.ecm.core.service;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sokolsoft.ecm.core.SokolException;
import com.sokolsoft.ecm.core.Utils;
import com.sokolsoft.ecm.core.enums.AuditRecordType;
import com.sokolsoft.ecm.core.model.*;
import com.sokolsoft.ecm.core.repository.*;
import com.sokolsoft.ecm.core.specification.SortOrder;
import com.sokolsoft.ecm.core.specification.Specification;
import com.sokolsoft.ecm.core.specification.SpecificationUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;

    private final UserRepository userRepository;

    private final ContragentRepository contragentRepository;

    private final ContragentPersonRepository contragentPersonRepository;

    private final UserService userService;

    private final SecurityService securityService;

    private final TaskRepository taskRepository;

    private final ApplicationContext applicationContext;

    private final AuditRecordRepository auditRecordRepository;

    private final ConfigService configService;

    private final ObjectMapper objectMapper;

    @Override
    public DocumentsPage getDocuments(Specification spec) {
        int pageNum = spec.getPage();

        Sort sort;
        if (spec.getSort() != null && spec.getSort().size() > 0) {
            sort = new Sort(spec.getSort().get(0).getOrder() == SortOrder.ASC
                    ? Sort.Direction.ASC : Sort.Direction.DESC, spec.getSort().get(0).getField());
        } else {
            sort = Sort.by("createDate");
        }

        PageRequest pageRequest = new PageRequest(pageNum, spec.getSize(), sort);

        Class documentClass = spec.getDocumentClass() != null ? spec.getDocumentClass() : Document.class;

        org.springframework.data.jpa.domain.Specification<?> specification = null;
        if (spec.getCondition() != null) {
            specification = SpecificationUtil.conditionToSpringSpecification(spec.getCondition(),
                    documentClass, spec.getJoin());
        }

        org.springframework.data.domain.Page repoPage = Task.class.equals(spec.getDocumentClass())
                ? taskRepository.findAll((org.springframework.data.jpa.domain.Specification<Task>) specification, pageRequest)
                : documentRepository.findAll((org.springframework.data.jpa.domain.Specification<Document>) specification, pageRequest);

        DocumentsPage page = new DocumentsPage();
        page.setTotalElements(repoPage.getTotalPages());
        page.setTotalPages(repoPage.getTotalPages());
        page.setSize(repoPage.getSize());

        List<?> content = repoPage.getContent();
        if (Document.class.isAssignableFrom(documentClass)) {
            content = processDocuments((List<Document>) content);
        } else if (documentClass.equals(Task.class)) {
            content = processTasks((List<Task>) content);
        } else {
            throw new RuntimeException("Cannot get content");
        }

        page.setContent(content);

        return page;
    }

    private List<?> processTasks(List<Task> content) {
        for (Task task : content) {
            switch (task.getStatus()) {
                case "execution":
                    task.setStatus("Исполнение");
                    break;
                case "done":
                    task.setStatus("Завершена");
                    break;
            }
        }

        return content;
    }

    private List<Document> processDocuments(List<Document> initialContent) {
        List<Document> content = new ArrayList<>(initialContent.size());

        for (Document document : initialContent) {
            Document clearedDocument = createDocumentByType(document.getDocumentType());
            copyAccessibleProperties(document, clearedDocument);
            content.add(clearedDocument);
        }

        return content;
    }

    @Data
    public static class DocumentsPage {
        private Integer totalElements;
        private Integer totalPages;
        private Integer size;
        private List<?> content;
    }

    @Override
    public Document getDocument(UUID documentId) {
        Document document = documentRepository.findById(documentId).orElse(null);
        if (document != null) {
            //todo check AR to read
            Document clearedDocument = createDocumentByType(document.getDocumentType());

            copyAccessibleProperties(document, clearedDocument);

            return clearedDocument;
        } else {
            throw new RuntimeException("Document not exist or no access rights");
        }
    }

    protected Document createDocumentByType(String documentType) {
        try {
            return (Document) Arrays.stream(Document.class.getAnnotation(JsonSubTypes.class).value())
                    .filter(t -> t.name().equals(documentType))
                    .map(JsonSubTypes.Type::value)
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("Cannot find document type"))
                    .getConstructor()
                    .newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("Cannot find document type", e);
        }
    }

    protected void copyAccessibleProperties(Document document, Document clearedDocument) {
        List<String> roles = securityService.getCurrentUserRoles();
        checkForDraft(document, roles);

        Map<String, String> fieldsRights = securityService.getFieldsRights(document.getDocumentType(), document.getStatus(), roles);
        BeanUtils.copyProperties(document, clearedDocument, Utils.getNotAccessibleReadablePropertyNames(document, fieldsRights));
//        BeanUtils.copyProperties(document, clearedDocument);
    }

    @SneakyThrows
    @Override
    public Document save(Document document) {
        Document oldDocument = documentRepository.findById(document.getId()).orElse(null);
        ObjectNode oldDocumentAsNode = objectMapper.valueToTree(oldDocument);

        List<String> roles = securityService.getCurrentUserRoles();
        checkForDraft(document, roles);

        Map<String, String> fieldsRights = securityService.getFieldsRights(document.getDocumentType(), document.getStatus(), roles);

        checkMandatoryFields(document, fieldsRights);
        BeanUtils.copyProperties(document, oldDocument, Utils.getNotAccessibleWritablePropertyNames(document, fieldsRights));

        fillTitles(oldDocument);

        if (oldDocument instanceof IncomingDocument) {
            createExternalOrganizationPersons((IncomingDocument) oldDocument);
        }
        ObjectNode documentAsNode = objectMapper.valueToTree(oldDocument);
        saveHistory(document.getId(), oldDocumentAsNode, documentAsNode);

        return (Document) documentRepository.save(oldDocument);
    }

    @SneakyThrows
    @Transactional
    protected void saveHistory(UUID documentId, ObjectNode oldDocument, ObjectNode document) {
        Set<String> fields = new LinkedHashSet<>();
        oldDocument.fieldNames().forEachRemaining(fields::add);
        document.fieldNames().forEachRemaining(fields::add);
        fields.forEach(f -> {
            JsonNode oldValue = oldDocument.get(f);
            JsonNode value = document.get(f);
            if (oldValue.equals(value)) {
                oldDocument.remove(f);
                document.remove(f);
            }
        });

        User currentUser = userService.getCurrentUser();
        ObjectNode data = objectMapper.createObjectNode();
        data.set("prev", oldDocument);
        data.set("current", document);
        auditRecordRepository.save(AuditRecord.builder()
                .objectId(documentId)
                .userId(currentUser.getId())
                .userTitle(currentUser.getTitle())
                .type(AuditRecordType.DOCUMENT_CHANGE)
                .createDate(Instant.now())
                .data(objectMapper.writeValueAsString(data))
                .build());
    }

    private void checkMandatoryFields(Document document, Map<String, String> fieldsRights) {
        /*
        В целом данный метод вызывается только для дополнительной проверке на стороне сервера,
        так как проверка должна производится на клиенте.
         */
        Class<? extends Document> documentClass = document.getClass();
        List<Field> fields = Utils.getFields(documentClass);
        fields.forEach(f -> {
            String ar = fieldsRights.containsKey(f.getName()) ? fieldsRights.get(f.getName()) : fieldsRights.get("*");
            if ("3".equals(ar)) {
                try {
                    f.setAccessible(true);
                    Object value = f.get(document);
                    if (value == null || (value instanceof String && StringUtils.isEmpty(value))) {
                        throw new SokolException("101", "Не заполнено обязательное поле \"" + f.getName() + "\"");
                    }
                } catch (IllegalAccessException e) {
                    log.error("Cannot access field", e);
                }
            }
        });
    }

    public void checkForDraft(Document document, List<String> roles) {
        User currentUser = userService.getCurrentUser();
        UUID creator = document.getCreator();
        if (currentUser.getId().equals(creator)) {
            roles.add("ROLE_AUTHOR");
        }
        String status = document.getStatus();
        if ("Черновик".equals(status) && !roles.contains("ROLE_AUTHOR")) {
            throw new RuntimeException("Document in draft state available only to author");
        }
    }

    private void createExternalOrganizationPersons(IncomingDocument document) {
        UUID externalOrganization = document.getExternalOrganization();
        if (externalOrganization != null) {
            String externalSigner = document.getExternalSigner();
            if (externalSigner != null && !externalSigner.isEmpty()) {
                List<ContragentPerson> signerPersons = contragentPersonRepository.findByOrganizationIdAndTitle(externalOrganization, externalSigner);
                if (signerPersons.size() == 0) {
                    ContragentPerson signerPerson = new ContragentPerson();
                    signerPerson.setId(UUID.randomUUID());
                    signerPerson.setTitle(externalSigner);
                    signerPerson.setOrganizationId(externalOrganization);
                    contragentPersonRepository.save(signerPerson);
                }
            }

            String externalExecutor = document.getExternalExecutor();
            if (externalExecutor != null && !externalExecutor.isEmpty()) {
                List<ContragentPerson> executorsPersons = contragentPersonRepository.findByOrganizationIdAndTitle(externalOrganization, externalExecutor);
                if (executorsPersons.size() == 0) {
                    ContragentPerson executorPerson = new ContragentPerson();
                    executorPerson.setId(UUID.randomUUID());
                    executorPerson.setTitle(externalExecutor);
                    executorPerson.setOrganizationId(externalOrganization);
                    contragentPersonRepository.save(executorPerson);
                }
            }
        }
    }

    private void fillTitles(Document document) {

        Class<? extends Document> documentClass = document.getClass();
        List<Field> fields = Utils.getFields(documentClass);
        Map<String, Field> fieldsMap = fields.stream().collect(Collectors.toMap(Field::getName, f -> f));

        fields.forEach(f -> {
            if (f.isAnnotationPresent(TitleField.class)) {
                TitleField titleField = f.getAnnotation(TitleField.class);
                String idFieldName = titleField.idField();
                String titleFieldName = titleField.titleField();
                Class<? extends CrudRepository> repositoryClass = titleField.repository();
                CrudRepository repository = applicationContext.getBean(repositoryClass);
                Function<Object, Object> fillValue = idValue -> {
                    Object object = repository.findById(idValue).orElse(null);
                    if (object == null) {
                        return null;
                    }
                    try {
                        Field targetTitleField = object.getClass().getDeclaredField(titleFieldName);
                        targetTitleField.setAccessible(true);
                        Object titleValue = targetTitleField.get(object);

                        return titleValue;
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                };

                try {
                    Field field = fieldsMap.get(idFieldName);
                    field.setAccessible(true);
                    Object idValue = field.get(document);

                    if (idValue == null) {
                        return;
                    }

                    f.setAccessible(true);
                    f.set(document, idValue instanceof List
                            ? ((List) idValue).stream().map(fillValue).collect(Collectors.toList())
                            : Optional.of(idValue).map(fillValue).orElse(null));

                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    @Override
    public UUID createDocument(String documentType) {
        Document document = null;

        document = createDocumentByType(documentType);

        UUID id = UUID.randomUUID();
        document.setId(id);
        document.setTitle("Новый документ");
        document.setStatus("Черновик");
        document.setDocumentType(documentType);
        document.setCreateDate(Instant.now());

        User currentUser = userService.getCurrentUser();
        document.setCreator(currentUser.getId());
        document.setCreatorTitle(currentUser.getTitle());
        document.setCreateDate(Instant.now());

        documentRepository.save(document);

        return id;
    }

    @Override
    public void moveDocumentToState(UUID documentId, String state) {
        Document document = documentRepository.getOne(documentId);
        document.setStatus(state);
        documentRepository.save(document);
    }

    @Override
    public Page<AuditRecord> getDocumentHistory(UUID documentId, Pageable pageable) {
        Document document = documentRepository.findById(documentId).orElse(null);
        if (document != null) {
            //todo check AR to read
            Page<AuditRecord> auditRecords = auditRecordRepository.findAllByObjectId(documentId, pageable);
            auditRecords.forEach(r -> {
                try {
                    ObjectNode node = objectMapper.readValue(r.getData(), ObjectNode.class);
                    ObjectNode prev = (ObjectNode) node.get("prev");
                    ObjectNode current = (ObjectNode) node.get("current");

                    Set<String> fields = new LinkedHashSet<>();
                    prev.fieldNames().forEachRemaining(fields::add);
                    current.fieldNames().forEachRemaining(fields::add);
                    ArrayNode fieldsArray = node.putArray("fields");
                    JsonNode documentType = configService.getPublicConfig("types/" + document.getDocumentType());
                    documentType.get("fields").forEach(f -> {
                        String id = f.get("id").asText();
                        if (fields.contains(id)) {
                            String title = f.get("title").asText();
                            ObjectNode field = objectMapper.createObjectNode();
                            field.put("id", id);
                            field.put("title", title);

                            fieldsArray.add(field);
                        }
                    });

                    //todo extract to method
                    List<String> roles = securityService.getCurrentUserRoles();
                    checkForDraft(document, roles);
                    Map<String, String> fieldsRights = securityService.getFieldsRights(document.getDocumentType(), document.getStatus(), roles);
                    
                    fields.forEach(f -> {
                        String level = fieldsRights.get(f);
                        if (level == null) {
                            level = fieldsRights.get("*");
                        }
                        if (!"1".equals(level)
                                && !"2".equals(level)
                                && !"3".equals(level)) {
                            if (prev.has(f)) {
                                prev.remove(f);
                            }
                            if (current.has(f)) {
                                current.remove(f);
                            }
                        }
                    });
                    r.setData(objectMapper.writeValueAsString(node));
                } catch (IOException e) {
                    log.error("Cannot read document history object", e);
                    r.setData(null);
                }
            });
            return auditRecords;
        } else {
            throw new RuntimeException("Document not exist or no access rights");
        }
    }
}
