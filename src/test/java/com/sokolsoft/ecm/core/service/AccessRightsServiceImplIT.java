package com.sokolsoft.ecm.core.service;

import com.sokolsoft.ecm.core.model.IncomingDocument;
import com.sokolsoft.ecm.core.model.User;
import com.sokolsoft.ecm.core.repository.DocumentRepository;
import com.sokolsoft.ecm.core.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class AccessRightsServiceImplIT {
    @Autowired
    DocumentRepository documentRepository;

    @Autowired
    AccessRightsService accessRightsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    public static final UUID TEST_USER_ID = UUID.fromString("ca49fdb1-84f1-414c-bc62-95646a28fd6c");

    public void activateContext() {
        SecurityContext securityContext = new SecurityContextImpl();
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_DIC_USERS_SAVE"));
        AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken("test",
                (Principal) () -> "test", grantedAuthorities);
        securityContext.setAuthentication(anonymousAuthenticationToken);

        SecurityContextHolder.setContext(securityContext);
    }

    public void addUser() {
        User u = new User();
        u.setId(TEST_USER_ID);
        u.setTitle("Test User");
        u.setFirstName("Test");
        u.setLastName("Test");
        u.setMiddleName("Test");
        u.setUsername("test");
        u.setEnabled(true);
        u.setPassword(passwordEncoder.encode("test"));
        userRepository.save(u);
    }

    @Test
    public void testActions() {
        activateContext();
        addUser();
        
        IncomingDocument document = new IncomingDocument();
        document.setId(UUID.fromString("9e8421d8-b7a0-4317-a866-f66fd69f44db"));
        document.setDocumentType("Входящий");
        document.setStatus("Черновик");
        document.setCreator(TEST_USER_ID);
        IncomingDocument savedDocument = documentRepository.save(document);

        List<String> actions = accessRightsService.getActionsForObject(savedDocument.getId(), "document");
        System.out.println("==================");
        actions.forEach(System.out::println);
        System.out.println("==================");

        SecurityContextHolder.clearContext();
    }
}
