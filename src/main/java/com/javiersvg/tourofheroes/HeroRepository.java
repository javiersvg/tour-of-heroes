package com.javiersvg.tourofheroes;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Optional;

@PreAuthorize("hasRole('ROLE_ADMIN')")
public interface HeroRepository extends MongoRepository<Hero, String> {

    @PreAuthorize("hasRole('ROLE_USER')")
    List<Hero> findByNameLike(@Param("name") String name);

    @PreAuthorize("hasRole('ROLE_USER')")
    @PostAuthorize("returnObject.owner == principal.id")
    @Override
    <S extends Hero> Optional<S> findOne(Example<S> example);

    @PreAuthorize("hasRole('ROLE_USER')")
    @Query("{'owner': ?#{ principal?.id }}")
    @Override
    Page<Hero> findAll(Pageable pageable);

    @PreAuthorize("hasRole('ROLE_USER')")
    @Override
    <S extends Hero> S save(S s);

    @PreAuthorize("hasRole('ROLE_USER')")
    @Override
    void delete(Hero hero);
}
