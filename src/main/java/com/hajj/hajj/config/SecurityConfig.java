package com.hajj.hajj.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    JWTFilter jwtFilter;

    @Autowired
    AuthenticationProvider authenticationProvider;

    @Autowired
    CorsConfigurationSource corsConfigurationSource;

    @Value("${authorized.ip}")
    String authorizedIP;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        // httpSecurity.formLogin(formLogin ->
        //     formLogin
        //             .loginPage("/login").successHandler(authSuccess).failureHandler(authFailure)
        //             .permitAll()
        // );

        return httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .cors(cors->cors.configurationSource(apiConfigurationSource()))
         .authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**").permitAll()
                //  .access(new WebExpressionAuthorizationManager("isAuthenticated() and hasIpAddress('"+authorizedIP+"')"))
                 .anyRequest()
                 .authenticated()
         )
         .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
         .exceptionHandling(exp->exp.authenticationEntryPoint(
            (request, response, authException) ->{
                    response.setContentType("application/json");
                    if(response.getStatus()==404){
                        response.getWriter().write(
                                """
                                       {
                                        "status":"failed",
                                        "error":"The Resource you requested can not be found"
                                       }
                                        """
                        );
                    }
                    else if(response.getStatus()==403){
                        response.getWriter().write(
                                """
                                       {
                                        "status":"failed",
                                        "error":"Invalid request"
                                       }
                                        """
                        );
                    }
            }
         ))
         .authenticationProvider(authenticationProvider)
         .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
         .build();

    }

    CorsConfigurationSource apiConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","HEAD","OPTION"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization","Content-Type","Origin"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



}
