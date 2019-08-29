package com.sokolsoft.ecm.core.model;

import lombok.Data;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.Instant;
import java.util.UUID;

@Entity
@DiscriminatorValue("INCOMING")
@Data
@ToString(callSuper = true)
public class IncomingDocument extends Document {
    protected Instant externalDate;

    protected String externalNumber;

    protected String externalSigner;

    protected String externalExecutor;

    protected UUID externalOrganization;

    protected String externalOrganizationTitle;
}
