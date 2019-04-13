package com.javiersvg.tourofheroes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class HeroControllerTest {

    private static final String TOKEN = token("Default");
    @Autowired
    private MockMvc mvc;

    @Test
    public void getHeroesShouldReturnForbiddenToUnregisteredUsers() throws Exception {
        this.mvc.perform(get("/heroes")).andExpect(status().isUnauthorized());
    }

    @Test
    public void getHeroesShouldAuthorizeToken() throws Exception {
        this.mvc.perform(get("/heroes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    public void getHeroesShouldAcceptLoggedUsers() throws Exception {
        this.mvc.perform(get("/heroes")
                .with(authentication(getAuthentication())))
                .andExpect(status().isOk());
    }

    private Authentication getAuthentication() {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        HashMap<String, Object> details = new HashMap<>();
        details.put(JwtClaimNames.JTI, "1");
        details.put("name", "Javier Mino");
        details.put("email", "javiermino@test.com");

        Jwt principal = new Jwt(TOKEN, Instant.now(), Instant.now().plusSeconds(10), Collections.singletonMap("alg", "RS256"), details);

        return new JwtAuthenticationToken(principal, authorities);
    }

    private static String token(String name) {
        try {
            return resource(name + ".token");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String resource(String suffix) throws IOException {
        String name = UserControllerTest.class.getSimpleName() + "-" + suffix;
        ClassPathResource resource = new ClassPathResource(name, UserControllerTest.class);
        try ( BufferedReader reader = new BufferedReader(new FileReader(resource.getFile())) ) {
            return reader.lines().collect(Collectors.joining());
        }
    }
}