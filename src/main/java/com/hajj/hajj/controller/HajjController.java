package com.hajj.hajj.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hajj.hajj.DTO.HujajRequest;
import com.hajj.hajj.DTO.ResponseDTO;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@CrossOrigin(origins = "http://10.11.0.46:3006")
@RestController
@RequestMapping("/api/v1")
public class HajjController {

    @Autowired
    Environment env;
    @Autowired
    HujjajRepo hujjajRepo;

    @Autowired
    UsersRepo usersRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/hajjList")
    public List<HUjjaj> getAllList(){
        return hujjajRepo.findAll();
    }
    @GetMapping("/get_hujaj/{payment_code}")
    public  Object get_hujaj(@PathVariable String payment_code) throws JsonProcessingException {
        final String apiUrl = env.getProperty("hajjApi")+ payment_code;

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-Authorization", env.getProperty("x-auth"));
        headers.add("x-Authorization-Secret", env.getProperty("x-secret"));
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        try {

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, requestEntity, String.class);
            // Process the response
           return  Objects.requireNonNull(response.getBody());
        }

        catch (HttpClientErrorException ex) {
            // Handle unauthorized error
            ObjectMapper objectMapper1 = new ObjectMapper();
            return  objectMapper1.readValue(ex.getMessage().replace("404 Not Found:","").trim().substring(0,ex.getMessage().replace("404 Not Found:","").length()-2).substring(1), ResponseDTO.class);
        }

    }

    @PreAuthorize("hasRole('maker')")
    @PostMapping("/make_hajj_trans")
    public Object make_hujaj_transaction(@RequestBody HujajRequest hujaj)

    {
        HUjjaj newHajj = new HUjjaj();
        newHajj.setFirst_name(hujaj.getFirst_name());
        newHajj.setLast_name(hujaj.getLast_name());
        newHajj.setMiddle_name(hujaj.getMiddle_name());
        newHajj.setPhone(hujaj.getPhone());
        newHajj.setPhoto_url(hujaj.getPhoto_url());
        newHajj.setPassport_number(hujaj.getPassport_number());
        newHajj.setBirth_date(hujaj.getBirth_date());
        newHajj.setService_package(hujaj.getService_package());
        newHajj.setPayment_code(hujaj.getPayment_code());
        newHajj.setPaid(false);
        newHajj.setAccount_number(hujaj.getAccount_number());
        newHajj.setAccount_holder(hujaj.getAccount_holder());
        newHajj.setTrans_ref_no(hujaj.getTrans_ref_no());
        String  bank_code= env.getProperty("bank_code");
        newHajj.setBranch_name(bank_code);
        Timestamp  date= Timestamp.valueOf(LocalDateTime.now());
        newHajj.setCreated_at(date);
        newHajj.setUpdated_at(date);
        double amount;
        double amount_inAccount;
        try {
            amount = Double.parseDouble(hujaj.getAmount());
            amount_inAccount = Double.parseDouble(hujaj.getAmount_inaccount());
        }
        catch(NumberFormatException | NullPointerException e){
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Please Enter a Valid Amount Value");
            error.put("status", "failed");
            return error;
        }

        Optional<Users> maker = usersRepo.findById(hujaj.getMaker_Id());
        if(maker.isEmpty()){
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Please Set Maker ID");
            error.put("status", "failed");
            return error;
        }

//Validation between amount and amount_in account
        if (amount <= amount_inAccount){
                newHajj.setMaker_Id(maker.get());
                hujjajRepo.save(newHajj);
                Map<String, Object> success = new HashMap<>();
                success.put("message", "Transaction Made Successfully");
                success.put("status", "Success");
                return success;
        }
        Map<String, Object> error = new HashMap<>();
        error.put("message", "You Don't Have Enough Account Balance To Make the Transaction");
        error.put("status", "failed");
        return error;
    }

    @PreAuthorize("hasRole('checker')")
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
