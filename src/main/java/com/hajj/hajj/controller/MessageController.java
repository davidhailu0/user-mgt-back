package com.hajj.hajj.controller;

import com.hajj.hajj.config.JWTUtil;
import com.hajj.hajj.model.Message;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.UsersRepo;
import com.hajj.hajj.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/message")
public class MessageController {
    @Autowired
    MessageService messageService;

    @Autowired
    JWTUtil util;

    @Autowired
    UsersRepo usersRepo;

    @GetMapping
    public List<Message> allMessages(){
        return messageService.getAllMessage();
    }

    @PostMapping("/approve/{id}")
    public Object approveMessage(@PathVariable Long id, HttpServletRequest request){
        Users admin = getUser(request);
        return messageService.approveMessage(id,admin);
    }

    Users getUser(HttpServletRequest request){
        String jwtToken = request.getHeader("Authorization");
        if(jwtToken==null||!jwtToken.startsWith("Bearer")){
            return null;
        }
        String username = util.validateTokenAndRetrieveSubject(jwtToken.split(" ")[1]);
        return usersRepo.findUsersByUsername(username).get();
    }
    
}
