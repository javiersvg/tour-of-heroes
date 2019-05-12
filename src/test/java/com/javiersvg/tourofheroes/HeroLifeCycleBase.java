package com.javiersvg.tourofheroes;

import com.jayway.jsonpath.JsonPath;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TourOfHeroesApplication.class)
public abstract class HeroLifeCycleBase {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Rule
    public TestName testName = new TestName();

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .apply(springSecurity())
                .alwaysDo(document(
                        getClass().getSimpleName() + "_" + testName.getMethodName()))
                .build();
        RestAssuredMockMvc.mockMvc(this.mockMvc);
    }

    protected String getDefaultUserToken() {
        return "Bearer " + token("Default");
    }

    private String token(String name) {
        try {
            return resource(name + ".token");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String resource(String suffix) throws IOException {
        String name = UserControllerTest.class.getSimpleName() + "-" + suffix;
        ClassPathResource resource = new ClassPathResource(name, UserControllerTest.class);
        try ( BufferedReader reader = new BufferedReader(new FileReader(resource.getFile())) ) {
            return reader.lines().collect(Collectors.joining());
        }
    }

    public String getHref() throws Exception {
        MvcResult mvcResult = this.mockMvc.perform(
                get("/heroes")
                        .header(HttpHeaders.AUTHORIZATION,  getDefaultUserToken()))
                .andReturn();
        return JsonPath.read(mvcResult.getResponse().getContentAsString(), "$._embedded.ex:heroes[0]._links.self.href");
    }
}
