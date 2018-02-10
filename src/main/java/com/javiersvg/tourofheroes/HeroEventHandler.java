package com.javiersvg.tourofheroes;

import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RepositoryEventHandler(Hero.class)
public class HeroEventHandler {

    @HandleBeforeCreate
    public void handeHeroSave(Hero hero) {
        AppUser appUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        hero.setOwner(appUser.getId());
    }
}