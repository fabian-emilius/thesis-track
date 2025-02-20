package de.tum.cit.aet.thesis.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
    private final JwtAuthConverter jwtAuthConverter;
    private final CorsConfig corsConfig;

    public WebSecurityConfig(JwtAuthConverter jwtAuthConverter, CorsConfig corsConfig) {
        this.jwtAuthConverter = jwtAuthConverter;
        this.corsConfig = corsConfig;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().configurationSource(corsConfig)
                .and()
                .authorizeHttpRequests()
                .requestMatchers("/v2/groups/*/published-theses/**").permitAll()
                .requestMatchers("/v2/groups/*/published-presentations/**").permitAll()
                .requestMatchers("/v2/groups").permitAll()
                .requestMatchers("/v2/groups/*/info").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(jwtAuthConverter)
                .and()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }
}