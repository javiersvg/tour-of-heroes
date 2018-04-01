package com.javiersvg.tourofheroes;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Map<String, Object> details = new HashMap<>();
        details.put("name", customUser.name());
        details.put("id", customUser.id());

        AppUser principal =
                new AppUser(details);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal, "password", createAuthorityList("ROLE_USER"));
        context.setAuthentication(auth);
        return context;
    }
}
