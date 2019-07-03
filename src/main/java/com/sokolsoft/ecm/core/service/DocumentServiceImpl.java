package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.Utils;
import com.sokolsoft.ecm.core.model.*;
import com.sokolsoft.ecm.core.repository.ContragentPersonRepository;
import com.sokolsoft.ecm.core.repository.ContragentRepository;
import com.sokolsoft.ecm.core.repository.DocumentRepository;
import com.sokolsoft.ecm.core.repository.UserRepository;
import com.sokolsoft.ecm.core.specification.SortOrder;
import com.sokolsoft.ecm.core.specification.Specification;
import com.sokolsoft.ecm.core.specification.SpecificationUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DocumentServiceImpl implements DocumentService {
    private DocumentRepository documentRepository;

    private UserRepository userRepository;

    private ContragentRepository contragentRepository;

    private ContragentPersonRepository contragentPersonRepository;

    private UserService userService;
    private SecurityService securityService;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository,
                               UserRepository userRepository, ContragentRepository contragentRepository,
                               ContragentPersonRepository contragentPersonRepository,
                               UserService userService,
                               SecurityService securityService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.contragentRepository = contragentRepository;
        this.contragentPersonRepository = contragentPersonRepository;
        this.userService = userService;
        this.securityService = securityService;
    }

    @Override
    public Page<Document> getDocuments(Specification spec) {
        int pageNum = spec.getPage();

        Sort sort;
        if (spec.getSort() != null && spec.getSort().size() > 0) {
            sort = new Sort(spec.getSort().get(0).getOrder() == SortOrder.ASC
                    ? Sort.Direction.ASC : Sort.Direction.DESC, spec.getSort().get(0).getField());
        } else {
            sort = Sort.by("createDate");
        }

        PageRequest pageRequest = new PageRequest(pageNum, spec.getSize(), sort);

        org.springframework.data.jpa.domain.Specification<Document> specification = null;
        if (spec.getCondition() != null) {
            specification = SpecificationUtil.conditionToSpringSpecification(spec.getCondition(), Document.class);
        }

        org.springframework.data.domain.Page<Document> repoPage = documentRepository.findAll(specification, pageRequest);

        return repoPage;
    }

    @Override
    public Document getDocument(UUID documentId) {
        Document document = documentRepository.findById(documentId).orElse(null);
        if (document != null) {
            Document clearedDocument = null;

            switch (document.getDocumentType()) {
                case "Входящий": clearedDocument = new IncomingDocument(); break;
                case "Тестовый": clearedDocument = new IncomingDocument(); break;
                case "Исходящий": clearedDocument = new OutgoingDocument(); break;
                case "Внутренний": clearedDocument = new InnerDocument(); break;
            }

            List<String> roles = securityService.getCurrentUserRoles();
            checkForDraft(document, roles);

            Map<String, String> fieldsRights = securityService.getFieldsRights(document.getDocumentType(), document.getStatus(), roles);
            BeanUtils.copyProperties(document, clearedDocument, Utils.getNotAccessibleReadablePropertyNames(document, fieldsRights));

            return clearedDocument;
        } else {
            throw new RuntimeException("Document not exist or no access rights");
        }
    }

    @Override
    public Document save(Document document) {
        Document oldDocument = documentRepository.findById(document.getId()).orElse(null);

        List<String> roles = securityService.getCurrentUserRoles();
        checkForDraft(document, roles);

        Map<String, String> fieldsRights = securityService.getFieldsRights(document.getDocumentType(), document.getStatus(), roles);
        BeanUtils.copyProperties(document, oldDocument, Utils.getNotAccessibleWritablePropertyNames(document, fieldsRights));

        if (oldDocument instanceof IncomingDocument) {
            fillTitles((IncomingDocument) oldDocument);
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

    private void fillTitles(IncomingDocument document) {
        UUID addresseeId = document.getAddressee();
        if (addresseeId != null) {
            User addressee = userRepository.getOne(addresseeId);
            document.setAddresseeTitle(addressee.getTitle());
        } else {
            document.setAddresseeTitle(null);
        }

        List<UUID> addresseeCopiesIds = document.getAddresseeCopies();
        List<String> addresseeCopiesTitles = new ArrayList<>();
        for (UUID addresseeCopyId : addresseeCopiesIds) {
            if (addresseeCopyId != null) {
                User addressee = userRepository.getOne(addresseeCopyId);
                addresseeCopiesTitles.add(addressee.getTitle());
            }
        }
        document.setAddresseeCopiesTitles(addresseeCopiesTitles);

        UUID externalOrganizationId = document.getExternalOrganization();
        if (externalOrganizationId != null) {
            Contragent externalOrganization = contragentRepository.getOne(externalOrganizationId);
            document.setExternalOrganizationTitle(externalOrganization.getTitle());
        } else {
            document.setExternalOrganizationTitle(null);
        }

        UUID registrarId = document.getRegistrar();
        if (registrarId != null) {
            User registrar = userRepository.getOne(registrarId);
            document.setRegistrarTitle(registrar.getTitle());
        } else {
            document.setRegistrarTitle(null);
        }

        UUID executorId = document.getExecutor();
        if (executorId != null) {
            User executor = userRepository.getOne(executorId);
            document.setExecutorTitle(executor.getTitle());
        } else {
            document.setExecutorTitle(null);
        }

        UUID controllerId = document.getController();
        if (controllerId != null) {
            User controller = userRepository.getOne(controllerId);
            document.setControllerTitle(controller.getTitle());
        } else {
            document.setControllerTitle(null);
        }
    }

    @Override
    public UUID createDocument(String documentType) {
        Document document = null;

        switch (documentType) {
            case "Входящий": document = new IncomingDocument(); break;
            case "Исходящий": document = new OutgoingDocument(); break;
            case "Внутренний": document = new InnerDocument(); break;
        }
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
}
