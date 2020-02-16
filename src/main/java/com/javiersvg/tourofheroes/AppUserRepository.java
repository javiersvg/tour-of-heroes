package com.javiersvg.tourofheroes;

import org.springframework.data.mongodb.repository.MongoRepository;

interface AppUserRepository extends MongoRepository<User, String> {
}
