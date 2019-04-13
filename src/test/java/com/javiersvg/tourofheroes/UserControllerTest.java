package com.javiersvg.tourofheroes;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.env.MockWebServerPropertySource;
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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String TOKEN = token("Default");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @Before
    public void setUp() throws Exception {
        appUserRepository.deleteAll();
    }

    @Test
    public void getUserShouldReturnForbiddenToUnregisteredUsers() throws Exception {
        this.mvc.perform(get("/user")).andExpect(status().isUnauthorized());
    }

    @Test
    public void getUserShouldAuthorizeToken() throws Exception {
        this.mvc.perform(get("/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .andExpect(status().isOk());
    }

    @Test
    public void getUserShouldSaveAuthorizedUser() throws Exception {
        this.mvc.perform(get("/user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .andExpect(status().isOk());
        assertThat(appUserRepository.findAll().size(), is(1));
    }

    @Test
    public void getUserShouldAcceptLoggedUsers() throws Exception {
        Authentication authentication = getAuthentication();
        this.appUserRepository.save(new User((Jwt)authentication.getPrincipal()));
        this.mvc.perform(get("/user")
                .with(authentication(authentication)))
                .andExpect(status().isOk());
    }

    private Authentication getAuthentication() {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("Everything");

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