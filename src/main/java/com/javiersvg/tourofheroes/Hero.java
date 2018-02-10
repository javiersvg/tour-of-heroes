package com.javiersvg.tourofheroes;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Hero {
    private String id;
    private String name;
    @JsonIgnore
    private String owner;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }
}
