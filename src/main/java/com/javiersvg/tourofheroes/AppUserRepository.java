package com.javiersvg.tourofheroes;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.oauth2.jwt.Jwt;

interface AppUserRepository extends MongoRepository<Jwt, String> {
}
