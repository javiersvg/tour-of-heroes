package com.javiersvg.tourofheroes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private AppUserRepository repository;

    @RequestMapping("/user")
    public Jwt getUser(Authentication authentication) {
        return repository.findOne(Example.of((Jwt) authentication.getPrincipal())).orElseThrow();
    }
}
