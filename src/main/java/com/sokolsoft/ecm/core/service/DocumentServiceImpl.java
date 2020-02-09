package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.Utils;
import com.sokolsoft.ecm.core.model.*;
import com.sokolsoft.ecm.core.repository.*;
import com.sokolsoft.ecm.core.specification.SortOrder;
import com.sokolsoft.ecm.core.specification.Specification;
import com.sokolsoft.ecm.core.specification.SpecificationUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;

    private final UserRepository userRepository;

    private final ContragentRepository contragentRepository;

    private final ContragentPersonRepository contragentPersonRepository;

    private final UserService userService;

    private final SecurityService securityService;

    private final TaskRepository taskRepository;

    private final ApplicationContext applicationContext;

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
        Document document = null;

        switch (documentType) {
            case "Входящий":
                document = new IncomingDocument();
                break;
            case "Тестовый":
                document = new IncomingDocument();
                break;
            case "Исходящий":
                document = new OutgoingDocument();
                break;
            case "Внутренний":
                document = new InnerDocument();
                break;
        }

        return document;
    }

    protected void copyAccessibleProperties(Document document, Document clearedDocument) {
        List<String> roles = securityService.getCurrentUserRoles();
        checkForDraft(document, roles);

        Map<String, String> fieldsRights = securityService.getFieldsRights(document.getDocumentType(), document.getStatus(), roles);
        BeanUtils.copyProperties(document, clearedDocument, Utils.getNotAccessibleReadablePropertyNames(document, fieldsRights));
//        BeanUtils.copyProperties(document, clearedDocument);
    }

    @Override
    public Document save(Document document) {
        Document oldDocument = documentRepository.findById(document.getId()).orElse(null);

        List<String> roles = securityService.getCurrentUserRoles();
        checkForDraft(document, roles);

        Map<String, String> fieldsRights = securityService.getFieldsRights(document.getDocumentType(), document.getStatus(), roles);
        //todo check mandatory fields #SOKOL-1020
        BeanUtils.copyProperties(document, oldDocument, Utils.getNotAccessibleWritablePropertyNames(document, fieldsRights));

        fillTitles(oldDocument);

        if (oldDocument instanceof IncomingDocument) {
            createExternalOrganizationPersons((IncomingDocument) oldDocument);
        }

        return (Document) documentRepository.save(oldDocument);
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
}
