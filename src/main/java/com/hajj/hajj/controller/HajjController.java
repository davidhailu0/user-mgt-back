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
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.core.ParameterizedTypeReference;


@CrossOrigin()
@RestController
@RequestMapping("/api/v1")
public class HajjController {

    @Autowired
    Environment env;
    @Autowired
    HujjajRepo hujjajRepo;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/get_nameQuery/{account_number}")
    public  Object get_nameQuery(@PathVariable String account_number) {

        final String name_Query_api = env.getProperty("name_Query")+ account_number;
		String token=env.getProperty("wso2Token");
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders headers =new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(name_Query_api, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> responseBody = responseEntity.getBody();

            if (responseBody != null) {
                Object status = responseBody.get("status");
                if ("FAILURE".equals(status)) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", responseBody.get("error_description"));
                    error.put("status", "Error");
                    return error;
                }
                else
                {
                    return responseBody;
                }
            }
            else {
                return  "Response body is empty or null";
            }
        }
        catch (HttpClientErrorException ex) {
            // Handle unauthorized error
           return ex.getMessage();
        }

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

    public void updateTable(HujajRequest hujajRequest){
        if(hujajRequest.getId()==null){

        }

    }


}
