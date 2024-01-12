package com.hajj.hajj.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    AuthSuccess authSuccess;

    @Autowired
    AuthFailure authFailure;

    @Autowired
    JWTFilter jwtFilter;


    @Value("${authorized.ip}")
    String authorizedIP;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // httpSecurity.formLogin(formLogin ->
        //     formLogin
        //             .loginPage("/login").successHandler(authSuccess).failureHandler(authFailure)
        //             .permitAll()
        // );
        httpSecurity.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
         .authorizeHttpRequests(auth -> auth.requestMatchers("api/auth/**").permitAll()
                //  .requestMatchers("/**")
                //  .access(new WebExpressionAuthorizationManager("isAuthenticated() and hasIpAddress('"+authorizedIP+"')"))
                 .anyRequest()
                 .authenticated()
         )
         .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        //  .userDetailsService(userService)
        //  .exceptionHandling(exc->exc.authenticationEntryPoint((req,res,auth)->res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")))
         .build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // @Bean
    // public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
    //     AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
    //     // authenticationManagerBuilder.userDetailsService(userService);
    //     return authenticationManagerBuilder.build();
    // }
}
