package com.javiersvg.tourofheroes;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @RequestMapping("/user")
    public AppUser getUser(Authentication authentication) {
        return (AppUser) authentication.getPrincipal();
    }
}
