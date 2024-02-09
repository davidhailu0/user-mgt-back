package com.hajj.hajj.controller;

import com.google.gson.Gson;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

//    @Value("${react.login}")
//    String redirectPage;

//    @Value("${react.dashboard}")
//    String dashboard;
//    @Autowired
//    LoggerService loggerService;

    @Autowired JWTUtil util;
    @Autowired UsersRepo usersRepo;

    @Autowired AuthenticationManager authenticationManager;

    @Autowired
    Gson gson;

    @PostMapping("/login")
    public Object loginHandler(@RequestBody LoginCredential body, HttpServletResponse resp){
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(body.getUsername().trim().toLowerCase(), body.getPassword());
            authenticationManager.authenticate(authInputToken);
            String token = util.generateToken(body.getUsername());
            LoginResponse response = new LoginResponse();
            Users user = usersRepo.findUsersByUsername(body.getUsername()).get();
            response.setUser(user);
            response.setToken(token);
            return response;
        }catch (AuthenticationException authExc){
            Map<String, Object> error = new HashMap<>();
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
    public Users getProfile(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String username = util.validateTokenAndRetrieveSubject(token.split(" ")[1]);
        Users user = usersRepo.findUsersByUsername(username).get();
        user.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_"+user.getRole().getName().toUpperCase())));
        //loggerService.createNewLog(user,request.getRequestURI(), gson.toJson(user));
        return user;
    }
//    @RequestMapping(value="/dashboard")
//    public ModelAndView dashboard(){
//        return new ModelAndView("redirect:"+dashboard);
//    }
}
