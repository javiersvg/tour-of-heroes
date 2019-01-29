package com.javiersvg.tourofheroes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;

@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class TourOfHeroesApplication {

    @Autowired
    private AppUserRepository appUserRepository;

	public static void main(String[] args) {
		SpringApplication.run(TourOfHeroesApplication.class, args);
	}

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @EventListener(InteractiveAuthenticationSuccessEvent.class)
    public void saveUser(InteractiveAuthenticationSuccessEvent event) {
        AppUser principal = (AppUser) event.getAuthentication().getPrincipal();
        appUserRepository.save(principal);
    }
}