package com.sokolsoft.ecm.core.model;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "com.sokolsoft.ecm.core.model.SUUIDGenerator"
    )
    private UUID id;

    @Column(name = "group_name")
    private String groupName;

    @ElementCollection
    @CollectionTable(name="group_authorities", joinColumns=@JoinColumn(name="group_id"))
    @Column(name="authority")
    private List<String> authorities;

    @ElementCollection
    @CollectionTable(name="group_members", joinColumns=@JoinColumn(name="group_id"))
    @Column(name="username")
    private List<String> member;
}
