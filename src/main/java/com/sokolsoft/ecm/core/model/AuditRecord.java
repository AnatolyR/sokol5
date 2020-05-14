package com.sokolsoft.ecm.core.model;

import com.sokolsoft.ecm.core.enums.AuditRecordType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_record")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditRecord {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "com.sokolsoft.ecm.core.model.SUUIDGenerator"
    )
    private UUID id;

    private Instant createDate;

    @Enumerated(EnumType.STRING)
    private AuditRecordType type;

    private UUID userId;

    private String userTitle;

    private UUID objectId;

    @Lob
    private String data;
}
