package com.sokolsoft.ecm.core.web;

import com.sokolsoft.ecm.core.model.Authority;
import com.sokolsoft.ecm.core.repository.AuthorityRepository;
import com.sokolsoft.ecm.core.repository.GroupRepository;
import com.sokolsoft.ecm.core.repository.UserRepository;
import com.sokolsoft.ecm.core.service.AccessRightsService;
import com.sokolsoft.ecm.core.service.SecurityService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final AccessRightsService accessRightsService;

    private final SecurityService securityService;

    private final AuthorityRepository authorityRepository;

    private final UserRepository userRepository;

    private final GroupRepository groupRepository;

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
        if (currentUserRoles.contains("ROLE_USER_ROLES_VIEW")) {
            actions.add("roles");
        }
        if (currentUserRoles.contains("ROLE_USER_ROLE_ADD")) {
            actions.add("role_add");
        }
        if (currentUserRoles.contains("ROLE_USER_ROLE_DEL")) {
            actions.add("role_del");
        }
        return actions;
    }

    @GetMapping(path = "/api/user/search/roles")
    @ResponseBody
    public Page<AuthorityObject> findByObjectIdEquals(@Param("userId") String userId, Pageable p) {
        List<String> rolesForObject = securityService.getCurrentUserRoles();
        if (rolesForObject.contains("ROLE_USER_ROLES_VIEW")) {

            com.sokolsoft.ecm.core.model.User user = userRepository.findById(UUID.fromString(userId))
                    .orElseThrow(() -> new RuntimeException("User has not roles"));
            if (user.getUsername() != null) {

                List<AuthorityObject> authorityObjects =
                        new ArrayList<>(authorityRepository.findAllByUsernameEquals(
                                user.getUsername()).stream().map(
                                a -> new AuthorityObject(a.getId(), a.getAuthority())).collect(Collectors.toList()));

                Stream<AuthorityObject> authorityObjectStream = groupRepository.findAllByMemberContaining(user.getUsername()).stream().flatMap(g -> g.getAuthorities().stream().map(a -> new AuthorityObject(a, g.getGroupName())));
                authorityObjects.addAll(authorityObjectStream.collect(Collectors.toList()));

                return new PageImpl<>(authorityObjects);
            }
        }
        return Page.empty();
    }

    @Data
    public static class AuthorityObject {

        public AuthorityObject() {
        }

        public AuthorityObject(UUID id, String authority) {
            this.id = id;
            this.title = authority;
        }

        public AuthorityObject(String authority, String groupName) {
            this.title = authority;
            this.groupName = groupName;
        }

        private UUID id;

        private String userId;

        private String title;

        private String groupName;
    }

    @DeleteMapping("/api/delete/role/{ids}")
    public void deleteUsers(@PathVariable String ids) {
        List<String> rolesForObject = securityService.getCurrentUserRoles();
        if (!rolesForObject.contains("ROLE_USER_ROLE_DEL")) {
            throw new RuntimeException("No rights to delete role(s)");
        }
        List<Authority> authorities = Arrays.stream(ids.split(","))
                .map(id -> {
                    Authority authority = new Authority();
                    authority.setId(UUID.fromString(id));
                    return authority;
                }).collect(Collectors.toList());
        authorityRepository.deleteAll(authorities);
    }

    @PostMapping("/api/role")
    public void addRole(@RequestBody AuthorityObject authorityObject) {
        List<String> rolesForObject = securityService.getCurrentUserRoles();
        if (!rolesForObject.contains("ROLE_USER_ROLE_ADD")) {
            throw new RuntimeException("No rights to add role");
        }

        String username = userRepository.findById(UUID.fromString(authorityObject.getUserId()))
                .orElseThrow(() -> new RuntimeException("No user exist")).getUsername();

        Authority authority = new Authority();
        authority.setAuthority(authorityObject.getTitle());
        authority.setUsername(username);
        authorityRepository.save(authority);
    }

}
