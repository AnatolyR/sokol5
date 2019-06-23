package com.sokolsoft.ecm.core.model;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("INNER")
public class InnerDocument extends Document {
}
