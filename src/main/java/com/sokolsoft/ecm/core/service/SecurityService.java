package com.sokolsoft.ecm.core.service;

import java.util.List;
import java.util.Map;

public interface SecurityService {
    Map<String, String> getFieldsRights(String documentType, String documentStatus, String role);

    Map<String, String> getFieldsRights(String documentType, String documentStatus, List<String> roles);
}
