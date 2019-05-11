package com.javiersvg.tourofheroes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class HeroIntegrationTest {

    private static final String TOKEN = token("Default");
    @Autowired
    private MockMvc mvc;
    @Autowired
    private MongoOperations mongoOperations;

    @Before
    public void setUp() {
        this.mongoOperations.dropCollection(Hero.class);
    }

    @Test
    public void getHeroesShouldReturnForbiddenToUnregisteredUsers() throws Exception {
        this.mvc.perform(get("/heroes"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void getHeroesShouldAuthorizeToken() throws Exception {
        this.mvc.perform(get("/heroes")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.ex:heroes").isEmpty());
    }

    @Test
    public void getHeroesShouldAcceptLoggedUsers() throws Exception {
        this.mvc.perform(get("/heroes")
                .with(authentication(getAuthentication())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.ex:heroes").isEmpty());
    }

    @Test
    public void shouldSaveHero() throws Exception {
        this.mvc.perform(
                post("/heroes")
                        .content(asJsonString(buildHero("Javier", "jhondoe@mail")))
                        .with(authentication(getAuthentication())))
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldReturnHeroes() throws Exception {
        this.shouldSaveHero();
        this.mvc.perform(
                get("/heroes")
                        .with(authentication(getAuthentication())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.ex:heroes").isNotEmpty())
                .andExpect(jsonPath("$._embedded.ex:heroes[0].name")
                        .value("Javier"));
    }

    @Test
    public void shouldReturnSingleHero() throws Exception {
        this.shouldSaveHero();
        MvcResult mvcResult = this.mvc.perform(
                get("/heroes")
                        .with(authentication(getAuthentication())))
                .andReturn();
        String href = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$._embedded.ex:heroes[0]._links.self.href");
        this.mvc.perform(
                get(href)
                        .with(authentication(getAuthentication())))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDeleteHero() throws Exception {
        this.shouldSaveHero();
        MvcResult mvcResult = this.mvc.perform(
                get("/heroes")
                        .with(authentication(getAuthentication())))
                .andReturn();
        String href = JsonPath.read(mvcResult.getResponse().getContentAsString(), "$._embedded.ex:heroes[0]._links.self.href");
        this.mvc.perform(
                delete(href)
                        .with(authentication(getAuthentication())))
                .andExpect(status().isNoContent());
    }

    private Authentication getAuthentication() {
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");

        HashMap<String, Object> details = new HashMap<>();
        details.put(JwtClaimNames.JTI, "1");
        details.put("name", "Jhon Doe");
        details.put("email", "jhondoe@mail");

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

    private Hero buildHero(String name, String owner) throws JsonProcessingException {
        Hero hero = new Hero();
        hero.setName(name);
        hero.setOwner(owner);
        return hero;
    }

    private String asJsonString(Hero hero) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(hero);
    }
}