package com.sokolsoft.ecm.core.service;

import java.util.List;

public interface AccessRightsService {
    List<String> getRolesForObject(String objectId, String objectType);
}
