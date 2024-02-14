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

    @Value("${from}")
    String from;

    @Value("${messagePassword}")
    String password;

    @Scheduled(fixedRate = 20000)
    public void SendMessage() {
        List<Message> unsentMessage = messageRepo.findUnsentMessages();
        for (Message msg : unsentMessage) {
            if (msg.getReceiver() != null) {
                sendGetRequest(msg);
            }
        }
    }

    public List<Message> getAllMessage() {
        return messageRepo.findUnsentMessages();
    }

    public Message saveMessage(UserDetail userDetail, String content, Users madeBy,Users checkedBy) {
        Message newMessage = new Message();
        newMessage.setSender(sender);
        newMessage.setReceiver(userDetail);
        newMessage.setContent(content);
        newMessage.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        newMessage.setCreatedBy(madeBy);
        newMessage.setCheckedBy(checkedBy);
        return messageRepo.save(newMessage);
    }
    private void sendGetRequest(Message message){

        String encodedQuery = UriComponentsBuilder.fromUriString(url)
                .queryParam("username", sender)
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
