package com.sokolsoft.ecm.core.service;


import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SecurityServiceImpl implements SecurityService {
    private String file;

    @Value("${sokol.documentSecurityRightsStore}")
    public void setFile(String file) {
        this.file = file;
    }

    //todo add cache
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
                List<String> paths = getPaths(documentType, documentStatus, role);
                paths.forEach(path -> {
                    if (line.startsWith(path)) {
                        String[] fieldAndLevel = line.substring(path.length()).split("=");
                        String field = fieldAndLevel[0];
                        String level = fieldAndLevel[1];

                        String prevLevel = levels.get(field);
                        if (prevLevel == null || level.compareTo(prevLevel) > 0 || level.endsWith("!")) {
                            levels.put(field, level.endsWith("!") ? level.substring(0, level.length() - 2) : level);
                        }
                    }
                });
            }
            return levels;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getPaths(String documentType, String documentStatus, String role) {
        return Arrays.asList(
                documentType + "/" + documentStatus + "/" + role + "/",
                documentType + "/" + documentStatus + "/" + "*" + "/",
                documentType + "/" + "*" + "/" + role + "/",
                documentType + "/" + "*" + "/" + "*" + "/",
                "*" + "/" + documentStatus + "/" + role + "/",
                "*" + "/" + documentStatus + "/" + "*" + "/",
                "*" + "/" + "*" + "/" + role + "/",
                "*" + "/" + "*" + "/" + "*" + "/"
        );
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

    @Override
    public List<String> getCurrentUserRoles() {
        Collection<? extends GrantedAuthority> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities();
        List<String> roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toCollection(ArrayList::new));

        return roles;
    }
}
