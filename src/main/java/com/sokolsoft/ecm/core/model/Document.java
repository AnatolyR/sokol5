package com.sokolsoft.ecm.core.model;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * A Document.
 */
@Entity
@Table(name = "document")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "document")
public class Document implements Serializable {

    @Id
    private UUID id;


    private UUID addressee;

    private String addresseeTitle;

    @ElementCollection
    @OrderColumn
    private List<UUID> addresseeCopies;

    @ElementCollection
    @OrderColumn
    private List<String> addresseeCopiesTitles;


    private String documentKind;

    private String documentSubKind;

    private Instant registrationDate;

    private Instant createDate;

    private String title;

    private String comment;

    private Instant externalDate;

    private String externalNumber;

    private String externalSigner;

    private String externalExecutor;

    private Integer pageCount;

    private Integer appendixCount;

    private String caseNumber;

    private String registrar;

    private String externalOrganization;

    private String documentNumber;

    private String deliveryMethod;

    private String executionDate;

    private String executor;

    private String controller;

    private String status;

    private String documentType;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAddressee() {
        return addressee;
    }

    public void setAddressee(UUID addressee) {
        this.addressee = addressee;
    }

    public String getAddresseeTitle() {
        return addresseeTitle;
    }

    public void setAddresseeTitle(String addresseeTitle) {
        this.addresseeTitle = addresseeTitle;
    }

    public List<UUID> getAddresseeCopies() {
        return addresseeCopies;
    }

    public void setAddresseeCopies(List<UUID> addresseeCopies) {
        this.addresseeCopies = addresseeCopies;
    }

    public List<String> getAddresseeCopiesTitles() {
        return addresseeCopiesTitles;
    }

    public void setAddresseeCopiesTitles(List<String> addresseeCopiesTitles) {
        this.addresseeCopiesTitles = addresseeCopiesTitles;
    }

    public String getDocumentKind() {
        return documentKind;
    }

    public void setDocumentKind(String documentKind) {
        this.documentKind = documentKind;
    }

    public String getDocumentSubKind() {
        return documentSubKind;
    }

    public void setDocumentSubKind(String documentSubKind) {
        this.documentSubKind = documentSubKind;
    }

    public Instant getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Instant registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Instant getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Instant createDate) {
        this.createDate = createDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getExternalDate() {
        return externalDate;
    }

    public void setExternalDate(Instant externalDate) {
        this.externalDate = externalDate;
    }

    public String getExternalNumber() {
        return externalNumber;
    }

    public void setExternalNumber(String externalNumber) {
        this.externalNumber = externalNumber;
    }

    public String getExternalSigner() {
        return externalSigner;
    }

    public void setExternalSigner(String externalSigner) {
        this.externalSigner = externalSigner;
    }

    public String getExternalExecutor() {
        return externalExecutor;
    }

    public void setExternalExecutor(String externalExecutor) {
        this.externalExecutor = externalExecutor;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getAppendixCount() {
        return appendixCount;
    }

    public void setAppendixCount(Integer appendixCount) {
        this.appendixCount = appendixCount;
    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public String getRegistrar() {
        return registrar;
    }

    public void setRegistrar(String registrar) {
        this.registrar = registrar;
    }

    public String getExternalOrganization() {
        return externalOrganization;
    }

    public void setExternalOrganization(String externalOrganization) {
        this.externalOrganization = externalOrganization;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getExecutionDate() {
        return executionDate;
    }

    public void setExecutionDate(String executionDate) {
        this.executionDate = executionDate;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
}