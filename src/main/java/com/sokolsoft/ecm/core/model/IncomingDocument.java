package com.sokolsoft.ecm.core.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.Instant;
import java.util.UUID;

@Entity
@DiscriminatorValue("INCOMING")
public class IncomingDocument extends Document {
    protected Instant externalDate;

    protected String externalNumber;

    protected String externalSigner;

    protected String externalExecutor;

    protected UUID externalOrganization;

    protected String externalOrganizationTitle;

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

    public UUID getExternalOrganization() {
        return externalOrganization;
    }

    public void setExternalOrganization(UUID externalOrganization) {
        this.externalOrganization = externalOrganization;
    }

    public String getExternalOrganizationTitle() {
        return externalOrganizationTitle;
    }

    public void setExternalOrganizationTitle(String externalOrganizationTitle) {
        this.externalOrganizationTitle = externalOrganizationTitle;
    }
}
