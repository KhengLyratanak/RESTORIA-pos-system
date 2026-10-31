package com.nak.demo.common.security;

import com.nak.demo.common.filter.JwtAuthenticationFilter;
import com.nak.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Cashiers can sell: see the menu, place, pay, print and look up orders. Everything that changes the menu,
     * prices, stock, users, or shows revenue is for admins only.
     */
    private static final String ADMIN = "ADMIN";

    @Autowired
    private UserService userService;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception{
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder
                .userDetailsService(userService)
                .passwordEncoder(this.passwordEncoder());

        return authBuilder.build();
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS ))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/me").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/me/password").authenticated()
                        .requestMatchers("/api/v1/users/**").hasRole(ADMIN)
                        .requestMatchers("/api/v1/reports/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/settings").hasRole(ADMIN)
                        .requestMatchers("/api/v1/stocks/**").hasRole(ADMIN)
                        .requestMatchers("/api/v1/suppliers/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**", "/api/v10/category/**").authenticated()
                        .requestMatchers("/api/v1/products/**", "/api/v10/category/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/*").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/**").hasRole(ADMIN)
                        .anyRequest().authenticated()
                )
                .authenticationManager(this.authenticationManager(http))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )

                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();
    }
}
