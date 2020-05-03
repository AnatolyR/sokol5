package com.sokolsoft.ecm.core;

public class SokolException  extends RuntimeException {
    private String code;

    public SokolException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

