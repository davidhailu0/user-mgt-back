package com.hajj.hajj.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    JWTFilter jwtFilter;

    @Autowired
    AuthenticationProvider authenticationProvider;


    @Value("${authorized.ip}")
    String authorizedIP;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
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
                                        "error":"You are not authorized to make this request"
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
