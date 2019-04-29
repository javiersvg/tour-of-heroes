package com.javiersvg.tourofheroes;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class WithMockCustomUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockCustomUser> {

    private static final String TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzdWJqZWN0IiwiZXhwIjo0NjgzODA1MTI4fQ.ULEPdHG-" +
            "MK5GlrTQMhgqcyug2brTIZaJIrahUeq9zaiwUSdW83fJ7W1IDd2Z3n4a25JY2uhEcoV95lMfccHR6y_2DLrNvfta22SumY9PEDF2pido" +
            "54LXG6edIGgarnUbJdR4rpRe_5oRGVa8gDx8FnuZsNv6StSZHAzw5OsuevSTJ1UbJm4UfX3wiahFOQ2OI6G-r5TB2rQNdiPHuNyzG5yz" +
            "nUqRIZ7-GCoMqHMaC-1epKxiX8gYXRROuUYTtcMNa86wh7OVDmvwVmFioRcR58UWBRoO1XQexTtOQq_t8KYsrPZhb9gkyW8x2bAQF-d0" +
            "J0EJY8JslaH6n4RBaZISww";

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Map<String, Object> details = new HashMap<>();
        details.put("name", customUser.name());
        details.put("email", customUser.email());

        Jwt principal = new Jwt(TOKEN, Instant.now(), Instant.now().plusSeconds(10), Collections.singletonMap("alg", "RS256"), details);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal, "password", createAuthorityList("ROLE_USER"));
        context.setAuthentication(auth);
        return context;
    }
}
