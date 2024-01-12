package com.hajj.hajj.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
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

@CrossOrigin
@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Value("${react.login}")
    String redirectPage;

//    @Value("${react.dashboard}")
//    String dashboard;
    @Autowired JWTUtil util;
    @Autowired UsersRepo usersRepo;
    @Autowired UserRoleRepo userRoleRepo;
    @Autowired AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public Object loginHandler(@RequestBody LoginCredential body){
        try {
            UsernamePasswordAuthenticationToken authInputToken =
                    new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword());
            authenticationManager.authenticate(authInputToken);
            String token = util.generateToken(body.getUsername());
            LoginResponse response = new LoginResponse();
            Users user = usersRepo.findUsersByUsername(body.getUsername()).get();
            response.setUsername(user.getUsername());
            response.setBranch(user.getBranch());
            // response.setRole(userRoleRepo.findByUser(user).isPresent()?userRoleRepo.findByUser(user).get():null);
            response.setToken(token);
            response.setStatus(user.getStatus());
            return response.toString();
        }catch (AuthenticationException authExc){
            Map<String, Object> error = new HashMap<>();
            error.put("status", "failed");
            error.put("message", "Invalid Username or Password");
            return error;
        }
    }

    @RequestMapping(value = "/logout",method = RequestMethod.GET)
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.invalidate();
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(null);
        return "redirect:/login";
    }

//    @RequestMapping(value="/dashboard")
//    public ModelAndView dashboard(){
//        return new ModelAndView("redirect:"+dashboard);
//    }
}
