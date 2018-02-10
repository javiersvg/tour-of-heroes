package com.javiersvg.tourofheroes;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ROLE_ADMIN')")
interface AppUserRepository extends MongoRepository<AppUser, String> {

    @PreAuthorize("hasRole('ROLE_USER')")
    @Override
    <S extends AppUser> S save(S s);
}
