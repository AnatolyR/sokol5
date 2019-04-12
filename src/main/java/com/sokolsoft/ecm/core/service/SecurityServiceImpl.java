package com.sokolsoft.ecm.core.service;


import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SecurityServiceImpl implements SecurityService {
    private String file;

    @Value( "${sokol.documentSecurityRightsStore}" )
    public void setFile(String file) {
        this.file = file;
    }

    @Override
    public Map<String, String> getFieldsRights(String documentType, String documentStatus, String role) {
        try {
            InputStream inputStream;
            if (file.startsWith("classpath:")) {
                inputStream = SecurityServiceImpl.class.getResourceAsStream(file.substring(10));
            } else {
                inputStream = new FileInputStream(file);
            }
            List<String> lines = IOUtils.readLines(inputStream, "utf-8");
            inputStream.close();
            Map<String, String> levels = new HashMap<>();
            for (String line : lines) {
                String path = documentType + "/" + documentStatus + "/" + role + "/";
                if (line.startsWith(path)) {
                    String[] fieldAndLevel = line.substring(path.length()).split("=");
                    String field = fieldAndLevel[0];
                    String level = fieldAndLevel[1];
                    levels.put(field, level);
                }
            }
            return levels;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, String> getFieldsRights(String documentType, String documentStatus, List<String> roles) {
        Map<String, String> rights = new HashMap<>();
        for (String role : roles) {
            Map<String, String> rightsForRole = getFieldsRights(documentType, documentStatus, role);
            for (String key : rightsForRole.keySet()) {
                String right = rightsForRole.get(key);
                if (rights.get(key) == null) {
                    rights.put(key, right);
                } else {
                    String rightPrev = rights.get(key);
                    if (right.compareTo(rightPrev) > 0) {
                        rights.put(key, right);
                    }
                }
            }
        }
        return rights;
    }
}
