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
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {
    private DocumentRepository documentRepository;

    private UserRepository userRepository;

    private ContragentRepository contragentRepository;

    private ContragentPersonRepository contragentPersonRepository;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository,
                               UserRepository userRepository, ContragentRepository contragentRepository,
                               ContragentPersonRepository contragentPersonRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.contragentRepository = contragentRepository;
        this.contragentPersonRepository = contragentPersonRepository;
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
        BeanUtils.copyProperties(document, oldDocument, Utils.getNullPropertyNames(document));

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
        }

        UUID registrarId = document.getRegistrar();
        if (registrarId != null) {
            User registrar = userRepository.getOne(registrarId);
            document.setRegistrarTitle(registrar.getTitle());
        }

        UUID executorId = document.getExecutor();
        if (executorId != null) {
            User executor = userRepository.getOne(executorId);
            document.setExecutorTitle(executor.getTitle());
        }

        UUID controllerId = document.getController();
        if (controllerId != null) {
            User controller = userRepository.getOne(controllerId);
            document.setControllerTitle(controller.getTitle());
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
        documentRepository.save(document);

        return id;
    }
}
