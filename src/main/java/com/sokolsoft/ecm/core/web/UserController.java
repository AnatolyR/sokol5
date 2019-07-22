package com.sokolsoft.ecm.core.web;

import com.sokolsoft.ecm.core.service.AccessRightsService;
import com.sokolsoft.ecm.core.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final AccessRightsService accessRightsService;

    private final SecurityService securityService;

    @GetMapping("/api/currentUser")
    public User getCurrentUser(Principal principal) {
        User user = new User();
        user.setName(principal.getName());
        return user;
    }

    public static class User {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @GetMapping(path = "/api/user/availableActions")
    @ResponseBody
    public List<String> getAvailableActions() {
        List<String> currentUserRoles = securityService.getCurrentUserRoles();
        List<String> actions = new ArrayList<>();
        if (currentUserRoles.contains("ROLE_DIC_USERS_SAVE")) {
            actions.add("edit");
            actions.add("add");
        }
        if (currentUserRoles.contains("ROLE_DIC_USERS_DEL")) {
            actions.add("del");
        }
        if (currentUserRoles.contains("ROLE_DIC_USERS_RESET_PASS")) {
            actions.add("reset_pass");
        }
        return actions;
    }
}
