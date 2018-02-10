package com.javiersvg.tourofheroes;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Map;

public class AppUser implements UserDetails {
    private String id;
    private String name;
    private String email;

    AppUser(Map<String, Object> stringObjectMap) {
        this.id = String.valueOf(stringObjectMap.get("id"));
        this.name = String.valueOf(stringObjectMap.get("name"));
        this.email = String.valueOf(stringObjectMap.get("email"));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }
}
