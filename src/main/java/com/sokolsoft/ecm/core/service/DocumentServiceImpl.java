package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.Utils;
import com.sokolsoft.ecm.core.model.Contragent;
import com.sokolsoft.ecm.core.model.ContragentPerson;
import com.sokolsoft.ecm.core.model.Document;
import com.sokolsoft.ecm.core.model.User;
import com.sokolsoft.ecm.core.repository.ContragentPersonRepository;
import com.sokolsoft.ecm.core.repository.ContragentRepository;
import com.sokolsoft.ecm.core.repository.DocumentRepository;
import com.sokolsoft.ecm.core.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<Document> getDocuments() {
        return documentRepository.findAll();
    }

    @Override
    public Document getDocument(UUID documentId) {
        return documentRepository.findById(documentId).orElse(null);
    }

    @Override
    public Document save(Document document) {
        Document oldDocument = documentRepository.getOne(document.getId());

        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
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

        BeanUtils.copyProperties(document, oldDocument, Utils.getAccessiblePropertyNames(document, fieldsRights)); 

        fillTitles(oldDocument);
        createExternalOrganizationPersons(oldDocument);

        return documentRepository.save(oldDocument);
    }

    private void createExternalOrganizationPersons(Document document) {
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
        Document document = new Document();
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
