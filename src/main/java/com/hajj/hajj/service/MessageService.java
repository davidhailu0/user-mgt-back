package com.hajj.hajj.service;


import com.hajj.hajj.model.*;
import com.hajj.hajj.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessageService {

    @Autowired
    MessageRepo messageRepo;

    @Autowired
    UserResetPasswordRepo userResetPasswordRepo;

    @Value("${messageURL}")
    String url;

    @Value("${sender}")
    String sender;

    @Value("${sender}")
    String from;

    @Value("${messagePassword}")
    String password;

    @Scheduled(fixedRate = 20000)
    public void SendMessage(){
        List<Message> unsentMessage = messageRepo.findUnsentMessages();
        for(Message msg:unsentMessage){
            if(msg.getReceiver()!=null){
                sendGetRequest(msg);
            }
        }
    }

    public List<Message> getAllMessage(){
        return messageRepo.findUnsentMessages();
    }

    public void saveMessage(UserDetail userDetail, String content,Users admin){
        Message newMessage = new Message();
        newMessage.setSender(sender);
        newMessage.setReceiver(userDetail);
        newMessage.setContent(content);
        newMessage.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        newMessage.setCreatedBy(admin);
        messageRepo.save(newMessage);
    }

    public Object approveMessage(Long id,Users admin){
        Message message = messageRepo.findById(id).orElse(null);
        if(message!=null){
            message.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
            message.setCheckedBy(admin);
            messageRepo.save(message);
            if(message.getCreatedBy().getId().equals(admin.getId())){
                Map<String,Object> error = new HashMap<>();
                error.put("success",false);
                error.put("error","You can not approve this password reset");
                return error;
            }
            UserResetPassword userResetPassword = userResetPasswordRepo.findUserResetDetailByUser(message.getReceiver().getUser()).orElse(null);
            if(userResetPassword!=null){
                userResetPassword.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
                userResetPassword.setChecker(admin);
                userResetPasswordRepo.save(userResetPassword);
            }
            Map<String,Object> success = new HashMap<>();
            success.put("success",true);
            success.put("message","You have successfully approved the reset password");
            return success;
        }
        Map<String,Object> error = new HashMap<>();
        error.put("success",false);
        error.put("error","There is not message with this id");
        return error;
    }

    private void sendGetRequest(Message message){

        String encodedQuery = UriComponentsBuilder.fromUriString(url)
                .queryParam("username", sender)
                .queryParam("password",password)
                .queryParam("password",password)
                .queryParam("from",from)
                .queryParam("to",message.getReceiver().getPhoneNumber())
                .queryParam("text",message.getContent())
                .build()
                .toUriString();
     RestTemplate restTemplate = new RestTemplate();
     try {
         ResponseEntity<String> response = restTemplate.getForEntity(encodedQuery, String.class);
         if (response.getStatusCode().is2xxSuccessful()) {
             message.setMessageStatus(true);
             message.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
             messageRepo.save(message);
         }
     }
     catch(ResourceAccessException ex){
         System.out.println("Please Make Sure your IP have the privilege ");
     }
    }
}
