package com.sokolsoft.ecm.core.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("OUTGOING")
public class OutgoingDocument extends Document {
}
