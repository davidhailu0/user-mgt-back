package com.hajj.hajj.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hajj.hajj.service.LoggerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.hajj.hajj.DTO.LoginCredential;
import com.hajj.hajj.DTO.LoginResponse;
import com.hajj.hajj.config.JWTUtil;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.UserRoleRepo;
import com.hajj.hajj.repository.UsersRepo;

@CrossOrigin("*")
@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired JWTUtil util;
    @Autowired UsersRepo usersRepo;

    @Autowired
    LoggerService loggerService;

    @Autowired AuthenticationManager authenticationManager;

    @Autowired
    ObjectMapper objectMapper;

    @PostMapping("/lockaccount/{username}")
    public Object lockAccount(@PathVariable String username,HttpServletRequest request) throws JsonProcessingException {
        Users user = usersRepo.findUsersByUsername(username).orElse(null);
        if(user!=null){
            user.setLocked(true);
            usersRepo.save(user);
            Map<String, Object> success = new HashMap<>();
            success.put("status", "success");
            success.put("message", "Your account has been locked");
            loggerService.createNewLog(null,request.getRequestURI(), objectMapper.writeValueAsString(success));
            return success;
        }
        Map<String, Object> error = new HashMap<>();
        error.put("status", "failed");
        error.put("message", "This Account does not exist");
        loggerService.createNewLog(null,request.getRequestURI(), objectMapper.writeValueAsString(error));
        return error;
    }

    @PostMapping("/login")
    public Object loginHandler(@RequestBody LoginCredential body, HttpServletResponse resp,HttpServletRequest request) throws JsonProcessingException {
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(body.getUsername().trim().toLowerCase(), body.getPassword());
            authenticationManager.authenticate(authInputToken);
            String token = util.generateToken(body.getUsername());
            LoginResponse response = new LoginResponse();
            Users user = usersRepo.findUsersByUsername(body.getUsername()).get();
            if(user.isLocked()){
                Map<String, Object> error = new HashMap<>();
                error.put("status", "failed");
                error.put("message", "Your Account is locked.");
                loggerService.createNewLog(null,request.getRequestURI(), objectMapper.writeValueAsString(error));
                resp.setStatus(403);
                return error;
            }
            if(user.getStatus().equals("Inactive")){
                Map<String, Object> error = new HashMap<>();
                error.put("status", "failed");
                error.put("message", "You are not approved. Please Contact IT Support");
                loggerService.createNewLog(null,request.getRequestURI(), objectMapper.writeValueAsString(error));
                 resp.setStatus(403);
                return error;
            }
            response.setUser(user);
            response.setToken(token);
            return response;
        }catch (AuthenticationException authExc){
            Map<String, Object> error = new HashMap<>();
            loggerService.createNewLog(null,request.getRequestURI(), objectMapper.writeValueAsString(error));
            error.put("status", "failed");
            error.put("message", "Invalid Username or Password");
            resp.setStatus(403);
            return error;
        }
    }

    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(null);
        authenticationManager.authenticate(null);
        return "redirect:/login";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/profile")
    public Users getProfile(HttpServletRequest request) throws JsonProcessingException {
        String token = request.getHeader("Authorization");
        String username = util.validateTokenAndRetrieveSubject(token.split(" ")[1]);
        Users user = usersRepo.findUsersByUsername(username).get();
        user.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().getName().toUpperCase())));
        loggerService.createNewLog(user,request.getRequestURI(), objectMapper.writeValueAsString(user));
        return user;
    }

}
