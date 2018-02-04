package com.javiersvg.tourofheroes;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Created by Javier on 1/10/2018.
 */
public interface HeroRepository extends MongoRepository<Hero, String> {

    List<Hero> findByNameLike(@Param("name") String name);
}
