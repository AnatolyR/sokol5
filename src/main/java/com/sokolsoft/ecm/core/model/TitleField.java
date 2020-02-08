package com.sokolsoft.ecm.core.model;

import org.springframework.data.repository.CrudRepository;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TitleField {
    String idField();
    Class<? extends CrudRepository> repository();
    String titleField() default "title";
}
