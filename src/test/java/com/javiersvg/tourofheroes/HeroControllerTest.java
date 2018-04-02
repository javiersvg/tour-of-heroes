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
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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
        this.mvc.perform(get("/heroes")).andExpect(status().isForbidden());
    }

    @Test
    public void getUserShouldAcceptLoggedUsers() throws Exception {
        this.mvc.perform(get("/heroes")
                .with(authentication(getOauthTestAuthentication()))
                .sessionAttr("scopedTarget.oauth2ClientContext", getOauth2ClientContext()))
                .andExpect(status().isOk());
    }

    private Authentication getOauthTestAuthentication() {
        return new OAuth2Authentication(getOauth2Request(), getAuthentication());
    }

    private OAuth2Request getOauth2Request() {
        String clientId = "oauth-client-id";
        Map<String, String> requestParameters = Collections.emptyMap();
        String redirectUrl = "http://my-redirect-url.com";
        Set<String> responseTypes = Collections.emptySet();
        Set<String> scopes = Collections.emptySet();
        Set<String> resourceIds = Collections.emptySet();
        Map<String, Serializable> extensionProperties = Collections.emptyMap();
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        return new OAuth2Request(requestParameters, clientId, authorities,
                true, scopes, resourceIds, redirectUrl, responseTypes, extensionProperties);
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

    private OAuth2ClientContext getOauth2ClientContext() {
        OAuth2ClientContext mockClient = mock(OAuth2ClientContext.class);
        when(mockClient.getAccessToken()).thenReturn(new DefaultOAuth2AccessToken("my-fun-token"));
        return mockClient;
    }
}