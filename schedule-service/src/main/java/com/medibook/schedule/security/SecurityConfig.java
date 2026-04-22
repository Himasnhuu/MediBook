package com.medibook.schedule.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web
        .builders.HttpSecurity;
import org.springframework.security.config.http
        .SessionCreationPolicy;
import org.springframework.security.core.userdetails
        .UserDetailsService;
import org.springframework.security.core.userdetails
        .UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication
        .UsernamePasswordAuthenticationFilter;

@Configuration
@Order(1)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            throw new UsernameNotFoundException(
                    "No UserDetailsService configured");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public — patients can view slots
                .requestMatchers(HttpMethod.GET,
                        "/slots/available").permitAll()
                .requestMatchers(HttpMethod.GET,
                        "/slots/provider/**").permitAll()
                .requestMatchers(HttpMethod.GET,
                        "/slots/**").permitAll()
                // Protected — require JWT
                .anyRequest().authenticated())
            .addFilterBefore(jwtAuthFilter,
                    UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}