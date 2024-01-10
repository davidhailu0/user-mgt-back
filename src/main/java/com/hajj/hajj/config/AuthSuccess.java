package com.hajj.hajj.config;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthSuccess implements AuthenticationSuccessHandler{

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
                UserDetailInfo authenticatedUserDetail = (UserDetailInfo) authentication.getPrincipal();
                String json = new Gson().toJson(authenticatedUserDetail);
                response.setContentType("application/json");
                response.addHeader("JSESSIONID",request.getSession().getId());
                response.getWriter().write("{\"status\":\"success\",\"data\":"+ json +"}");
    }
    
}
