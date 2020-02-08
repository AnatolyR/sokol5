package com.sokolsoft.ecm.core.model;

import com.sokolsoft.ecm.core.repository.UserRepository;
import lombok.Data;

import javax.persistence.*;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@DiscriminatorValue("INNER")
@Data
public class InnerDocument extends Document {
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    protected List<UUID> approvers;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    @TitleField(idField = "approvers", repository = UserRepository.class)
    protected List<String> approversTitles;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    protected List<UUID> signers;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    @TitleField(idField = "signers", repository = UserRepository.class)
    protected List<String> signersTitles;

    protected String forDocumentNumber;

    protected Instant forRegistrationDate;
}
