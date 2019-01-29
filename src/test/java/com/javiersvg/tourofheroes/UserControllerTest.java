package com.javiersvg.tourofheroes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AppUserRepository appUserRepository;

    @Test
    public void getUserShouldReturnForbiddenToUnregisteredUsers() throws Exception {
        this.mvc.perform(get("/user")).andExpect(status().isUnauthorized());
    }

    @Test
    public void getUserShouldAcceptLoggedUsers() throws Exception {
        this.mvc.perform(get("/user")
                .with(authentication(getAuthentication())))
                .andExpect(status().isOk());
    }

    private Authentication getAuthentication() {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("Everything");

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