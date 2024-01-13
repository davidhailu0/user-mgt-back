package com.hajj.hajj.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hajj.hajj.model.HUjjaj;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.HujjajRepo;
import com.hajj.hajj.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1")
public class HajjController {

    @Autowired
    Environment env;
    @Autowired
    HujjajRepo hujjajRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping(value = "/get_hujaj")
    public  Object get_hujaj(@RequestBody HUjjaj hUjjaj)
    {
        final String apiUrl = env.getProperty("hajjApi")+ hUjjaj.getPayment_code();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-Authorization", env.getProperty("x-auth"));
        headers.add("x-Authorization-Secret", env.getProperty("x-secret"));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, String.class);
            // Process the response
           return  response.getBody();
        }

        catch (HttpClientErrorException ex) {
            // Handle unauthorized error
           return  ex.getMessage();
        }

    }

    @PostMapping("/make_hajj_trans")
    public Object make_hujaj_transaction(@RequestBody HUjjaj hUjjaj)

    {
        String  first_name= hUjjaj.getFirst_name();
        String  last_name= hUjjaj.getFirst_name();
        String  middle_name= hUjjaj.getFirst_name();
        String  phone= hUjjaj.getFirst_name();
        String  photo_url= hUjjaj.getFirst_name();
        String  passport_number= hUjjaj.getFirst_name();
        String  birth_date= hUjjaj.getFirst_name();
        String  service_package= hUjjaj.getFirst_name();
        String  payment_code= hUjjaj.getPayment_code();
        boolean  paid= false;
        String  bank_code= env.getProperty("bank_code");
        String  account_number= hUjjaj.getAccount_number();
        String  account_holder= hUjjaj.getAccount_holder();
        String  trans_ref_no= hUjjaj.getTrans_ref_no();
        Timestamp  date= Timestamp.valueOf(LocalDateTime.now());
        String  amount= hUjjaj.getAmount();

//Validation between amount and amount_in account
        // if(amount)

//POst
        hujjajRepo.save(new HUjjaj());


        return null;
    }

    @PostMapping("/Check_hajj_trans")
    public  Object CHECK_hujaj_transaction()
    {
//   Accept values from user and check the transaction
//        call wso2 end point post transaction
//        update mysql table based oon payment code
//        and post to hajj seerver

        return null;
    }


}
