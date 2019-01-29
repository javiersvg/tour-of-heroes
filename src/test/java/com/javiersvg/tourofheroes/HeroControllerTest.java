package com.javiersvg.tourofheroes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class HeroControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void getUserShouldReturnForbiddenToUnregisteredUsers() throws Exception {
        this.mvc.perform(get("/heroes")).andExpect(status().isUnauthorized());
    }

    @Test
    public void getUserShouldAcceptLoggedUsers() throws Exception {
        this.mvc.perform(get("/heroes")
                .with(authentication(getAuthentication())))
                .andExpect(status().isOk());
    }

    private Authentication getAuthentication() {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        HashMap<String, Object> details = new HashMap<>();
        details.put("id", "1");
        details.put("name", "Javier Mino");
        details.put("email", "javiermino@test.com");

        AppUser principal = new AppUser(details);

        TestingAuthenticationToken token = new TestingAuthenticationToken(principal, null, authorities);
        token.setAuthenticated(true);
        token.setDetails(details);

        return token;
    }
}