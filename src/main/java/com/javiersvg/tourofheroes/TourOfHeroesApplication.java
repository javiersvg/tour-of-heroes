package com.javiersvg.tourofheroes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.Collection;

@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class TourOfHeroesApplication extends WebSecurityConfigurerAdapter {

    @Autowired
    private AppUserRepository appUserRepository;

	public static void main(String[] args) {
		SpringApplication.run(TourOfHeroesApplication.class, args);
	}

    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .mvcMatchers("/browser/**").anonymous()
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(grantedAuthoritiesExtractor());
    }

    private Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor() {
        return new GrantedAuthoritiesExtractor();
    }

    static class GrantedAuthoritiesExtractor extends JwtAuthenticationConverter {
        protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
            return AuthorityUtils.createAuthorityList("ROLE_USER");
        }
    }

    @Bean
    public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
        return new SecurityEvaluationContextExtension();
    }

    @EventListener(AuthenticationSuccessEvent.class)
    public void saveUser(AuthenticationSuccessEvent event) {
	    try {
            Jwt principal = (Jwt) event.getAuthentication().getPrincipal();
            appUserRepository.save(new User(principal));
        } catch (Exception e) {

        }
    }
}