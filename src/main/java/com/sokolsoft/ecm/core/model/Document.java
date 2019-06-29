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
//@org.springframework.data.elasticsearch.annotations.Document(indexName = "document")
public abstract class Document implements Serializable {

    @Id
    protected UUID id;

    protected UUID addressee;

    protected String addresseeTitle;

    @ElementCollection
    @OrderColumn
    protected List<UUID> addresseeCopies;

    @ElementCollection
    @OrderColumn
    protected List<String> addresseeCopiesTitles;

    protected String documentKind;

    protected String documentSubKind;

    protected Instant registrationDate;

    protected Instant createDate;

    protected String title;

    protected String comment;

    protected Integer pageCount;

    protected Integer appendixCount;

    protected String caseNumber;

    protected UUID registrar;

    protected String registrarTitle;

    protected String documentNumber;

    protected String deliveryMethod;

    protected String executionDate;

    protected UUID executor;

    protected String executorTitle;

    protected UUID controller;

    protected String controllerTitle;

    protected String status;

    protected String documentType;

    protected UUID creator;

    protected String creatorTitle;

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

    public UUID getRegistrar() {
        return registrar;
    }

    public void setRegistrar(UUID registrar) {
        this.registrar = registrar;
    }

    public String getRegistrarTitle() {
        return registrarTitle;
    }

    public void setRegistrarTitle(String registrarTitle) {
        this.registrarTitle = registrarTitle;
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

    public UUID getExecutor() {
        return executor;
    }

    public void setExecutor(UUID executor) {
        this.executor = executor;
    }

    public String getExecutorTitle() {
        return executorTitle;
    }

    public void setExecutorTitle(String executorTitle) {
        this.executorTitle = executorTitle;
    }

    public UUID getController() {
        return controller;
    }

    public void setController(UUID controller) {
        this.controller = controller;
    }

    public String getControllerTitle() {
        return controllerTitle;
    }

    public void setControllerTitle(String controllerTitle) {
        this.controllerTitle = controllerTitle;
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

    public UUID getCreator() {
        return creator;
    }

    public void setCreator(UUID creator) {
        this.creator = creator;
    }

    public String getCreatorTitle() {
        return creatorTitle;
    }

    public void setCreatorTitle(String creatorTitle) {
        this.creatorTitle = creatorTitle;
    }
}
