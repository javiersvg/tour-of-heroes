package com.javiersvg.tourofheroes;

import org.springframework.data.annotation.Id;
import org.springframework.security.oauth2.jwt.Jwt;

public class User {
    private Jwt jwt;

    public User(Jwt jwt) {
        this.jwt = jwt;
    }

    @Id
    public String getId() {
        return this.jwt.getId();
    }

    public String getName() {
        return (String) this.jwt.getClaims().get("name");
    }

    public String getImageUrl() {
        return (String) this.jwt.getClaims().get("picture");
    }
}
