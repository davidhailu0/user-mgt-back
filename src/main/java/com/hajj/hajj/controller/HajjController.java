package com.hajj.hajj.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hajj.hajj.model.HUjjaj;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.HujjajRepo;
import com.hajj.hajj.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;


@CrossOrigin(origins = "http://10.11.0.46:3006")
@RestController
@RequestMapping("/api/v1")
public class HajjController {

    @Autowired
    Environment env;
    @Autowired
    HujjajRepo hujjajRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/get_hujaj/{payment_code}")
    public  Object get_hujaj(@PathVariable String payment_code)
    {
        final String apiUrl = env.getProperty("hajjApi")+ payment_code;

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
        double amount;
        double amount_inAccount;
        try {
            amount = Double.parseDouble(hUjjaj.getAmount());
            amount_inAccount = Double.parseDouble(hUjjaj.getAmount_inaccount());
        }
        catch(NumberFormatException | NullPointerException e){
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Please Enter a Valid Amount Value");
            error.put("status", "failed");
            return error;
        }

//Validation between amount and amount_in account
        if (amount <= amount_inAccount){
                hujjajRepo.save(hUjjaj);
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

    @PostMapping("/Check_hajj_trans")
    public  Object CHECK_hujaj_transaction(@RequestBody HUjjaj hUjjaj)
    {

//   Accept values from user and check the transaction
//        call wso2 end point post transaction


        String amount=hUjjaj.getAmount();
        String  draccount=hUjjaj.getAccount_number();
        String  naration= hUjjaj.getNARRATION();
        String paymentcode=hUjjaj.getPayment_code();

        String apiUrl=env.getProperty("fundtransAPi");
        String token=env.getProperty("wso2Token");
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders headers=new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        String jsonBody = "{ " +
                "\"Amount\":" +"\""+ amount + "\"" + ","+
                "\"Draccount\":" +"\""+ draccount + "\"" + ","+
                "\"Narrative\": " +"\""+ naration + "\"" + ","+
                "\"PaymentCode\": " +"\""+ paymentcode + "\"" +

                "}";
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody,headers);
        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> responseBody = responseEntity.getBody();

            if (responseBody != null) {
                Object MSGSTAT = responseBody.get("MSGSTAT");
                if ("FAILURE".equals(MSGSTAT)) {
                    Object EDESC = responseBody.get("EDESC");
                    String EDESC_s = EDESC.toString();
                    return  EDESC_s;
                }
                else
                {
                    return naration;
                }
            }
            else {
                return  "Response body is empty or null";
            }
        }
        catch (HttpClientErrorException ex) {
            // Handle unauthorized error
          return   ex.getMessage();
        }




//        update mysql table based oon payment code
//        and post to hajj seerver

    }


}
