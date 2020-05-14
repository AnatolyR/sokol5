package com.sokolsoft.ecm.core.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AuditRecordType {
    DOCUMENT_CREATE("Создание документа"),
    DOCUMENT_CHANGE("Изменение документа");

    private final String title;

    AuditRecordType(String title) {
        this.title = title;
    }

    @JsonValue
    public String getTitle() {
        return title;
    }
}
