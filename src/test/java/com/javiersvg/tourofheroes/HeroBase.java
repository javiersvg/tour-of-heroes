package com.javiersvg.tourofheroes;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TourOfHeroesApplication.class)
public abstract class HeroBase {

    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation();

    @Rule
    public TestName testName = new TestName();

    @Autowired
    WebApplicationContext context;

    @Before
    public void setup() {
        RestAssuredMockMvc.mockMvc(MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .apply(springSecurity())
                .alwaysDo(document(
                        getClass().getSimpleName() + "_" + testName.getMethodName()))
                .build());
    }
}
