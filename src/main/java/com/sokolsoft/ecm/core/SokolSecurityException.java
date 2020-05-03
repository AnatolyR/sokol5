package com.sokolsoft.ecm.core;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN)
public class SokolSecurityException extends RuntimeException {
    private String missedRole;

    public SokolSecurityException(String missedRole, String message) {
        super(message);
        this.missedRole = missedRole;
    }
}
