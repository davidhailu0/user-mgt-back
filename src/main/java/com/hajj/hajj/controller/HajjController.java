package com.hajj.hajj.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hajj.hajj.DTO.HujajRequest;
import com.hajj.hajj.DTO.ResponseDTO;
import com.hajj.hajj.config.JWTUtil;
import com.hajj.hajj.model.HUjjaj;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.HujjajRepo;
import com.hajj.hajj.repository.UsersRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.server.ResponseStatusException;


@CrossOrigin()
@RestController
@RequestMapping("/api/v1")
public class HajjController {

    @Autowired
    Environment env;
    @Autowired
    HujjajRepo hujjajRepo;

    @Autowired
    UsersRepo usersRepo;

    @Autowired
    JWTUtil util;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/get_nameQuery/{account_number}")
    public  Object get_nameQuery(@PathVariable String account_number) {

        final String name_Query_api = env.getProperty("name_Query") + account_number;
        String token = env.getProperty("wso2Token");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(name_Query_api, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {
            });

            Map<String, Object> responseBody = responseEntity.getBody();

            if (responseBody != null) {
                Object status = responseBody.get("status");
                if ("FAILURE".equals(status)) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("message", responseBody.get("error_description"));
                    error.put("status", "Error");
                    return error;
                } else {
                    return responseBody;
                }
            } else {
                return "Response body is empty or null";
            }
        } catch (HttpClientErrorException ex) {
            // Handle unauthorized error
            return ex.getMessage();
        }
    }
    @GetMapping("/hajjList")
    public List<HUjjaj> getAllList(){
        return hujjajRepo.findAll();
    }


    @GetMapping("/hajjList/paid")
    public List<HUjjaj> getAllPaid(){
        return hujjajRepo.findHUjjajByPaidStatus(true);
    }

    @GetMapping("/hajjList/unpaid")
    public List<HUjjaj> getAllUnpaid(){
        return hujjajRepo.findHUjjajByPaidStatus(false);
    }
    @GetMapping("/payment_code/{payment_code}")
    public Optional<HUjjaj> getHajjByPaymentCode(@PathVariable String payment_code, HttpServletResponse resp){
        return Optional.ofNullable(hujjajRepo.findHUjjajByPaymentCode(payment_code).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not Found")));
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
            return ex.getMessage();

        }

    }

//    @PreAuthorize("hasRole('maker')")
    @PostMapping("/make_hajj_trans")
    public Object make_hujaj_transaction(@RequestBody HUjjaj hujaj,HttpServletRequest request)

    {
        String  bank_code= env.getProperty("bank_code");
        Timestamp  date= Timestamp.valueOf(LocalDateTime.now());
        hujaj.setCreated_at(date);
        hujaj.setUpdated_at(date);
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

        String jwtToken = request.getHeader("Authorization");
        String username = util.validateTokenAndRetrieveSubject(jwtToken.split(" ")[1]);
        Users user = usersRepo.findUsersByUsername(username).get();

        if (amount <= amount_inAccount){
                hujaj.setMaker_Id(user);
                try {
                    Optional<HUjjaj> fetchHujaj = hujjajRepo.findHUjjajByPaymentCode(hujaj.getPayment_code());
                    if(fetchHujaj.isEmpty())
                    {
                        hujjajRepo.save(hujaj);
                    }
                    else {
                        Map<String, Object> error = new HashMap<>();
                        error.put("message", "The Payment Code "+hujaj.getPayment_code()+" is already registered");
                        error.put("status", "failed");
                        return error;
                    }

                }
                catch(Exception e){
                    return  e.getMessage();
                }
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
    public  Object CHECK_hujaj_transaction(@RequestBody HujajRequest hUjjaj, HttpServletRequest request,Users checker)
    {

//   Accept values from user and check the transaction
//        call wso2 end point post transaction
        String jwtToken = request.getHeader("Authorization");
        String username = util.validateTokenAndRetrieveSubject(jwtToken.split(" ")[1]);
        Users user = usersRepo.findUsersByUsername(username).get();

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

    public boolean updateTable(String paymentCode,HUjjaj hujajRequest,Users checker){
        Optional<HUjjaj> updatedHujaj = hujjajRepo.findHUjjajByPaymentCode(paymentCode);
        if(updatedHujaj.isPresent()){
            HUjjaj hujjaj = updatedHujaj.get();
            hujjaj.setEXTERNAL_REF_NO(hujajRequest.getEXTERNAL_REF_NO());
            hujjaj.setTrans_ref_no(hujjaj.getTRN_REF_NO());
            hujjaj.setAC_BRANCH(hujajRequest.getAC_BRANCH());
            hujjaj.setBranch_name(hujajRequest.getBranch_name());
            hujjaj.setNARRATION(hujajRequest.getNARRATION());
            hujjaj.setCUST_NAME(hujajRequest.getCUST_NAME());
            hujjaj.setTRN_REF_NO(hujajRequest.getTRN_REF_NO());
            hujjaj.setAC_NO(hujajRequest.getAC_NO());
            hujjaj.setLCY_AMOUNT(hujajRequest.getLCY_AMOUNT());
            hujjaj.setRELATED_CUSTOMER(hujajRequest.getRELATED_CUSTOMER());
            hujjaj.setRELATED_ACCOUNT(hujajRequest.getRELATED_ACCOUNT());
            hujjaj.setTRN_DT(hujajRequest.getTRN_DT());
            hujjaj.setVALUE_DT(hujajRequest.getVALUE_DT());
            hujjaj.setUSERID(hujajRequest.getUSERID());
            hujjaj.setAVLDAYS(hujajRequest.getAVLDAYS());
            hujjaj.setAUTH_ID(hujajRequest.getAUTH_ID());
            hujjaj.setSTMT_DT(hujajRequest.getSTMT_DT());
            hujjaj.setNODE(hujajRequest.getNODE());
            hujjaj.setAC_CCY(hujajRequest.getAC_CCY());
            hujjaj.setAUTH_TIMESTAMP(hujajRequest.getAUTH_TIMESTAMP());
            hujjaj.setChecker_Id(checker);
            hujjajRepo.save(hujjaj);
            return true;

        }
        return false;
    }

public  Object Post_to_hajserver(@RequestBody HUjjaj hUjjaj)
{

    String amount=hUjjaj.getAmount();
    String  account_number=hUjjaj.getAccount_number();
    String  account_holder=hUjjaj.getAccount_holder();
    String  naration= hUjjaj.getNARRATION();
    String paymentcode=hUjjaj.getPayment_code();
    String bank_code =env.getProperty("bank_code");
    String refrence_number=hUjjaj.getTrans_ref_no();
    String date=hUjjaj.getTRN_DT();


    final  String apiUrl=env.getProperty("hajjApi_post");
    RestTemplate restTemplate=new RestTemplate();
      HttpHeaders headers =  new HttpHeaders();
    headers.add("x-Authorization", env.getProperty("x-auth"));
    headers.add("x-Authorization-Secret", env.getProperty("x-secret"));
    headers.setContentType(MediaType.APPLICATION_JSON);
    String jsonBody = "{ " +
            "\"payment_code\":" +"\""+ paymentcode + "\"" + ","+
            "\"paid\":" +"\""+ true + "\"" + ","+
            "\"bank_code\": " +"\""+ bank_code + "\"" + ","+
            "\"account_number\": " +"\""+ account_number + "\"" +
            "\"account_holder\":" +"\""+ account_holder + "\"" + ","+
            "\"refrence_number\":" +"\""+ true + "\"" + ","+
            "\"date\": " +"\""+ date + "\"" + ","+
            "\"amount\": " +"\""+ amount + "\"" +

            "}";

    HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody,headers);
    try {
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> responseBody = responseEntity.getBody();

        if (responseBody != null) {
            return  responseBody;
        }
        else {
            return  "Response body is empty or null";
        }
    }
    catch (HttpClientErrorException ex) {
        // Handle unauthorized error
        return   ex.getMessage();
    }

}

}
