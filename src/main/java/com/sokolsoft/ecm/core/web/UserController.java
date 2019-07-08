package com.sokolsoft.ecm.core.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserController {


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
}
