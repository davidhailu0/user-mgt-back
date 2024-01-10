package com.hajj.hajj.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    AuthSuccess authSuccess;

    @Autowired
    AuthFailure authFailure;

    @Value("${authorized.ip}")
    String authorizedIP;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.formLogin(formLogin ->
            formLogin
                    .loginPage("/login").successHandler(authSuccess).failureHandler(authFailure)
                    .permitAll()
        );
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
         .authorizeHttpRequests(auth -> auth.requestMatchers("/login/**","/logout/**").permitAll()
                 .requestMatchers("/**")
                 .access(new WebExpressionAuthorizationManager("isAuthenticated() and hasIpAddress('"+authorizedIP+"')"))
                 .anyRequest()
                 .authenticated()
         ).build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

}
