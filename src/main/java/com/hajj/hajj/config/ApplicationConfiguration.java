package com.hajj.hajj.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hajj.hajj.model.UserRole;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.UserRoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hajj.hajj.repository.UsersRepo;

import java.util.List;
import java.util.Optional;

@Configuration
public class ApplicationConfiguration {

    @Autowired
    UsersRepo usersRepo;

    @Autowired
    UserRoleRepo userRoleRepo;

    @Bean
    UserDetailsService userDetailsService() {
        return username -> {
            Optional<Users> user = usersRepo.findUsersByUsername(username);
            if(user.isEmpty()){
                throw new UsernameNotFoundException("User not found");
            }
            Users userVerified = user.get();
            userVerified.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_" + userVerified.getRole().getName())));
            return userVerified;
        };
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }

    @Bean
    Gson getGson(){
        return new Gson();
    }
}
