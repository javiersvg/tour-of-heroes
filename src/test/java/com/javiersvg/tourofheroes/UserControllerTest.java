package com.javiersvg.tourofheroes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class UserControllerTest {

    private static final String TOKEN = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJzdWJqZWN0IiwiZXhwIjo0NjgzODA1MTI4fQ.ULEPdHG-" +
            "MK5GlrTQMhgqcyug2brTIZaJIrahUeq9zaiwUSdW83fJ7W1IDd2Z3n4a25JY2uhEcoV95lMfccHR6y_2DLrNvfta22SumY9PEDF2pido" +
            "54LXG6edIGgarnUbJdR4rpRe_5oRGVa8gDx8FnuZsNv6StSZHAzw5OsuevSTJ1UbJm4UfX3wiahFOQ2OI6G-r5TB2rQNdiPHuNyzG5yz" +
            "nUqRIZ7-GCoMqHMaC-1epKxiX8gYXRROuUYTtcMNa86wh7OVDmvwVmFioRcR58UWBRoO1XQexTtOQq_t8KYsrPZhb9gkyW8x2bAQF-d0" +
            "J0EJY8JslaH6n4RBaZISww";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AppUserRepository appUserRepository;

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
        assertThat(appUserRepository.findAll().size(), is(3));
    }

    @Test
    public void getUserShouldAcceptLoggedUsers() throws Exception {
        Authentication authentication = getAuthentication();
        this.appUserRepository.save((Jwt)authentication.getPrincipal());
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
}