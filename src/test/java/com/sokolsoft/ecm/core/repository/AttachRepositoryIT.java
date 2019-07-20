package com.sokolsoft.ecm.core.repository;

import com.sokolsoft.ecm.core.model.Attach;
import com.sokolsoft.ecm.core.model.AttachContent;
import com.sokolsoft.ecm.core.model.User;
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
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AttachRepositoryIT {

    @Autowired
    private AttachRepository attachRepository;

    @Autowired
    private AttachContentRepository attachContentRepository;

    @Test
    public void test() {
        SecurityContext securityContext = new SecurityContextImpl();

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_SYSTEM"));
        AnonymousAuthenticationToken anonymousAuthenticationToken = new AnonymousAuthenticationToken("test", new User(), grantedAuthorities);
        securityContext.setAuthentication(anonymousAuthenticationToken);

        SecurityContextHolder.setContext(securityContext);

        AttachContent attachContent = new AttachContent();
        UUID attachContentId = UUID.randomUUID();
        attachContent.setId(attachContentId);
        attachContent.setContent("test content".getBytes());
        attachContentRepository.save(attachContent);

        Attach attach = new Attach();
        attach.setTitle("Test 1");
        attach.setAttachContentId(attachContentId);

        attachRepository.save(attach);

        System.out.println(">>>> " + attachRepository.findAll().get(0));
        System.out.println(">>>> " + attachRepository.findById(attachContentId));

        SecurityContextHolder.clearContext();
    }
}
