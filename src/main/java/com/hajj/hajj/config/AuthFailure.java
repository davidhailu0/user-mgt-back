package com.hajj.hajj.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailure implements AuthenticationFailureHandler{

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
                response.setContentType("application/json");
        response.setStatus(404);
        response.getWriter().write("{" +
                "\"status\":\"failed\","+
                "\"message\":\"Invalid Username or Password\""
                +"}");
    }
    
}
