package com.javiersvg.tourofheroes;

import org.assertj.core.util.Arrays;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.data.domain.Example.of;
import static org.springframework.data.domain.PageRequest.of;

@RunWith(SpringRunner.class)
@DataMongoTest
public class HeroRepositoryTest {

    @Autowired
    private HeroRepository heroRepository;

    @Test
    @WithMockCustomUser
    public void findByNameLikeShouldReturnElementWhenAvailable() {
        this.heroRepository.save(buildHero("Javier", "user@mail"));
        this.heroRepository.save(buildHero("Marilyn", "user@mail"));
        this.heroRepository.save(buildHero("Seba", "2@mail"));

        List<Hero> heroes = this.heroRepository.findByNameLike("Mar");
        assertThat(heroes.size(), is(1));
        assertThat(heroes.get(0).getName(), is("Marilyn"));
    }

    @Test
    @WithMockCustomUser(email = "3@mail")
    public void findOneShouldReturnElementWhenAvailable() {
        this.heroRepository.save(buildHero("Javier", "3@mail"));
        Hero savedHero = this.heroRepository.save(buildHero("Marilyn", "3@mail"));
        this.heroRepository.save(buildHero("Seba", "2@mail"));
        Optional<Hero> foundHero = this.heroRepository.findOne(of(savedHero));
        assertThat(foundHero.orElseThrow().getName(), is("Marilyn"));
    }

    @Test
    @WithMockCustomUser(email = "4@mail")
    public void findAllShouldReturnAllOwnedElements() {
        this.heroRepository.save(buildHero("Javier", "4@mail"));
        this.heroRepository.save(buildHero("Marilyn", "4@mail"));
        this.heroRepository.save(buildHero("Seba", "2@mail"));

        Page<Hero> heroes = this.heroRepository.findAll(of(0,10));
        assertThat(heroes.getTotalElements(), is(2L));
        Matcher<Hero>[] matchers = Arrays.array(hasProperty("name", is("Marilyn")), hasProperty("name", is("Javier")));
        assertThat(heroes.getContent(), hasItems(matchers));
    }

    @Test
    @WithMockCustomUser(email = "5@mail")
    public void saveShouldStoreRetrievableElement() {
        this.heroRepository.save(buildHero("Javier", "5@mail"));
        Hero savedHero = this.heroRepository.save(buildHero("Marilyn", "5@mail"));
        this.heroRepository.save(buildHero("Seba", "2@mail"));
        Optional<Hero> foundHero = this.heroRepository.findOne(of(savedHero));
        assertThat(foundHero.orElseThrow().getName(), is("Marilyn"));
    }

    @Test
    @WithMockCustomUser(email = "6@mail")
    public void deleteShouldStopSavedElementFromBeingReturned() {
        this.heroRepository.save(buildHero("Javier", "6@mail"));
        Hero savedHero = this.heroRepository.save(buildHero("Marilyn", "6@mail"));
        this.heroRepository.save(buildHero("Seba", "2@mail"));
        Optional<Hero> foundHero = this.heroRepository.findOne(of(savedHero));
        assertThat(foundHero.orElseThrow().getName(), is("Marilyn"));

        this.heroRepository.delete(savedHero);
        Page<Hero> heroes = this.heroRepository.findAll(of(0, 10));
        assertThat(heroes.getTotalElements(), is(1L));
    }

    private Hero buildHero(String name, String owner) {
        Hero hero2 = new Hero();
        hero2.setName(name);
        hero2.setOwner(owner);
        return hero2;
    }
}