package com.javiersvg.tourofheroes;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by Javier on 1/10/2018.
 */
public interface HeroRepository extends MongoRepository<Hero, String> {
}
