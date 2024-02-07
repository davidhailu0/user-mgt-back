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

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.UsersRepo;
import com.hajj.hajj.service.LoggerService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    JWTFilter jwtFilter;

    @Autowired
    JWTUtil util;

    @Autowired
    UsersRepo usersRepo;

    @Autowired
    AuthenticationProvider authenticationProvider;


//    @Value("${authorized.ip}")
//    String authorizedIP;

    @Autowired
    LoggerService loggerService;

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
                    if(response.getStatus()==400){
                        loggerService.createNewLog(getUser(request).isPresent()?getUser(request).get():null, request.getRequestURI(), """
                            {
                             "success":false,
                             "error":"Please Check Your URL"
                            }
                             """);
                        response.getWriter().write(
                                """
                                       {
                                        "success":false,
                                        "error":"Please Check Your URL"
                                       }
                                        """
                        );
                    }
                    else if(response.getStatus()==401){
                        loggerService.createNewLog(null, request.getRequestURI(), """
                            {
                             "success":false,
                             "error":"You are not authorized to make this request"
                            }
                             """);
                        response.getWriter().write(
                                """
                                       {
                                        "success":false,
                                        "error":"You are not authorized to make this request"
                                       }
                                        """
                        );
                    }
                    else if(response.getStatus()==403){
                        loggerService.createNewLog(null, request.getRequestURI(), """
                            {
                             "success":false,
                             "error":"access to the requested resource is forbidden"
                            }
                             """);
                        response.getWriter().write(
                                """
                                       {
                                        "success":false,
                                        "error":"access to the requested resource is forbidden"
                                       }
                                        """
                        );
                    }
                    else if(response.getStatus()==404){

                        loggerService.createNewLog(getUser(request).isPresent()?getUser(request).get():null, request.getRequestURI(), """
                            {
                             "success":false,
                             "error":"The Resource you requested can not be found"
                            }
                             """);
                        response.getWriter().write(
                                """
                                       {
                                        "success":false,
                                        "error":"The Resource you requested can not be found"
                                       }
                                        """
                        );
                    }
                    
                    else{
                        response.setStatus(400);
                        loggerService.createNewLog(getUser(request).isPresent()?getUser(request).get():null, request.getRequestURI(), """
                            {
                             "success":false,
                             "error":"Inval"
                            }
                             """);
                        response.getWriter().write(
                                """
                                       {
                                        "success":false,
                                        "error":"Please Check Your URL or Request Body"
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


    Optional<Users> getUser(HttpServletRequest request)throws TokenExpiredException,JWTDecodeException,JWTVerificationException{
     String jwtToken = request.getHeader("Authorization");
     if(jwtToken.split(" ").length==2){
        String username = util.validateTokenAndRetrieveSubject(jwtToken.split(" ")[1]);
        return usersRepo.findUsersByUsername(username);
     }
     return Optional.empty();
 }



}
