package com.spotifyplaylist_aigenerator_backend.spotifyplaylist_aigenerator_backend.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter) {
        this.jwtTokenFilter = jwtTokenFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/**").permitAll()
                        .requestMatchers("/api/user/login/**").permitAll()
                        .requestMatchers("/api/user/register/**").permitAll()
                        .requestMatchers("/api/user/{loggedInUser}/user").authenticated()
                        .requestMatchers("/api/stripe/{loggedInUser}/checkoutsession").authenticated()
                        .requestMatchers("/api/stripe/success").permitAll()
                        .requestMatchers("/api/spotify/{loggedInUser}/playlist").authenticated()
                        .requestMatchers("/api/spotify/{loggedInUser}/top-ten-tracks").authenticated()
                        .requestMatchers("/api/spotify/callback").permitAll()
                        .requestMatchers("/api/spotify/{loggedInUser}/login").permitAll()
                        .requestMatchers("/api/user/users").permitAll()
                        .requestMatchers("/api/chat/**").authenticated()
                        .requestMatchers("/api/ai/**").authenticated())

                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}