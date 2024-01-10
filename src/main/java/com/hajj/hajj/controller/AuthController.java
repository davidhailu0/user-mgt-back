package com.hajj.hajj.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@CrossOrigin
@Controller
@RequestMapping("")
public class AuthController {

    @Value("${react.login}")
    String redirectPage;

//    @Value("${react.dashboard}")
//    String dashboard;
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public ModelAndView login(){
        return new ModelAndView("redirect:"+redirectPage);
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
