package com.javiersvg.tourofheroes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.DefaultCurieProvider;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.oauth2.core.ClaimAccessor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import java.util.Collection;

@SpringBootApplication
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class TourOfHeroesApplication {

    @Autowired
    private AppUserRepository appUserRepository;

	public static void main(String[] args) {
		SpringApplication.run(TourOfHeroesApplication.class, args);
	}

	@Configuration
    @Order(Ordered.HIGHEST_PRECEDENCE)
	public class HeroApiWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/heroes/**")
                    .csrf().disable()
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .oauth2ResourceServer()
                    .jwt()
                    .jwtAuthenticationConverter(grantedAuthoritiesExtractor());
        }

        private Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor() {
            return new GrantedAuthoritiesExtractor();
        }
    }

    @Configuration
    @Order(Ordered.HIGHEST_PRECEDENCE + 1)
    public class UserApiWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/user/**")
                    .authorizeRequests()
                    .anyRequest().authenticated()
                    .and()
                    .oauth2ResourceServer()
                    .jwt()
                    .jwtAuthenticationConverter(grantedAuthoritiesExtractor());
        }

        private Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor() {
            return new GrantedAuthoritiesExtractor();
        }
    }

    @Configuration
    public class HalBrowserWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .mvcMatchers("/favicon.ico").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .oauth2Login()
                    .and()
                    .headers()
                    .addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN));
        }
    }

    static class GrantedAuthoritiesExtractor extends JwtAuthenticationConverter {

	    @Override
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
        ClaimAccessor appUser = (ClaimAccessor) event.getAuthentication().getPrincipal();
        appUserRepository.save(new User(appUser));
    }

    @Bean
    public CurieProvider curieProvider() {
        return new DefaultCurieProvider("ex", new UriTemplate("/docs/{rel}.html"));
    }
}