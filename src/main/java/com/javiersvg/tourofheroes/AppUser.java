package com.javiersvg.tourofheroes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Map;

public class AppUser implements UserDetails {
    private String id;
    private String name;
    private String email;
    private final String picture;

    AppUser(Map<String, Object> stringObjectMap) {
        this.id = String.valueOf(stringObjectMap.get("id"));
        this.name = String.valueOf(stringObjectMap.get("name"));
        this.email = String.valueOf(stringObjectMap.get("email"));
        this.picture = String.valueOf(stringObjectMap.get("picture"));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @JsonIgnore
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

    public String getPicture() {
        return picture;
    }
}
