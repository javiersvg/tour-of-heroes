package com.javiersvg.tourofheroes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataMongoTest
public class HeroRepositoryTest {

    @Autowired
    private HeroRepository heroRepository;

    @Test
    @WithMockCustomUser
    public void findByNameLikeShouldReturnElementWhenAvailable() {
        this.heroRepository.save(buildHero("Javier", "1"));
        this.heroRepository.save(buildHero("Marilyn", "1"));
        this.heroRepository.save(buildHero("Seba", "2"));

        List<Hero> heroes = this.heroRepository.findByNameLike("Mar");
        assertThat(heroes.size(), is(1));
        assertThat(heroes.get(0).getName(), is("Marilyn"));
    }

    @Test
    @WithMockCustomUser(id = 3)
    public void findOneShouldReturnElementWhenAvailable() {
        this.heroRepository.save(buildHero("Javier", "3"));
        Hero savedHero = this.heroRepository.save(buildHero("Marilyn", "3"));
        this.heroRepository.save(buildHero("Seba", "2"));
        Hero foundHero = this.heroRepository.findOne(savedHero.getId());
        assertThat(foundHero.getName(), is("Marilyn"));
    }

    @Test
    @WithMockCustomUser(id = 4)
    public void findAllShouldReturnAllOwnedElements() {
        this.heroRepository.save(buildHero("Javier", "4"));
        this.heroRepository.save(buildHero("Marilyn", "4"));
        this.heroRepository.save(buildHero("Seba", "2"));

        Page<Hero> heroes = this.heroRepository.findAll(new PageRequest(0,10));
        assertThat(heroes.getTotalElements(), is(2L));
        assertThat(heroes.getContent(), hasItems(hasProperty("name", is("Marilyn")), hasProperty("name", is("Javier"))));
    }

    @Test
    @WithMockCustomUser(id = 5)
    public void saveShouldStoreRetrievableElement() {
        this.heroRepository.save(buildHero("Javier", "5"));
        Hero savedHero = this.heroRepository.save(buildHero("Marilyn", "5"));
        this.heroRepository.save(buildHero("Seba", "2"));
        Hero foundHero = this.heroRepository.findOne(savedHero.getId());
        assertThat(foundHero.getName(), is("Marilyn"));
    }

    @Test
    @WithMockCustomUser(id = 6)
    public void deleteShouldStopSavedElementFromBeingReturned() {
        this.heroRepository.save(buildHero("Javier", "6"));
        Hero savedHero = this.heroRepository.save(buildHero("Marilyn", "6"));
        this.heroRepository.save(buildHero("Seba", "2"));
        Hero foundHero = this.heroRepository.findOne(savedHero.getId());
        assertThat(foundHero.getName(), is("Marilyn"));

        this.heroRepository.delete(savedHero);
        Page<Hero> heroes = this.heroRepository.findAll(new PageRequest(0, 10));
        assertThat(heroes.getTotalElements(), is(1L));
    }

    private Hero buildHero(String name, String owner) {
        Hero hero2 = new Hero();
        hero2.setName(name);
        hero2.setOwner(owner);
        return hero2;
    }
}