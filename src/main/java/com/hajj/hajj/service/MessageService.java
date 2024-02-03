package com.hajj.hajj.service;

import com.hajj.hajj.model.Message;
import com.hajj.hajj.repository.MessageRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.net.ConnectException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    MessageRepo messageRepo;

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
            sendGetRequest(msg);
        }
    }

    public void saveMessage(String receiver,String content){
        Message newMessage = new Message();
        newMessage.setSender(sender);
        newMessage.setReceiver(receiver);
        newMessage.setContent(content);
        newMessage.setUpdated_at(Timestamp.valueOf(LocalDateTime.now()));
        messageRepo.save(newMessage);
    }

    private void sendGetRequest(Message message){

        String encodedQuery = UriComponentsBuilder.fromUriString(url)
                .queryParam("username", sender)
                .queryParam("password",password)
                .queryParam("password",password)
                .queryParam("from",from)
                .queryParam("to",message.getReceiver())
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
