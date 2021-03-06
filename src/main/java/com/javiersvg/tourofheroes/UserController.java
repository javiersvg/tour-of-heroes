package com.javiersvg.tourofheroes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private AppUserRepository repository;

    @GetMapping("/user")
    public User getUser(Authentication authentication) {
        return repository.findOne(Example.of(new User((Jwt) authentication.getPrincipal()))).orElseThrow();
    }
}
