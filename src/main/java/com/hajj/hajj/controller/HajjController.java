package com.hajj.hajj.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hajj.hajj.config.JWTUtil;
import com.hajj.hajj.model.HUjjaj;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.HujjajRepo;
import com.hajj.hajj.repository.UserDetailRepo;
import com.hajj.hajj.repository.UserRoleRepo;
import com.hajj.hajj.repository.UsersRepo;
import com.hajj.hajj.service.LoggerService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


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
    UserDetailRepo userDetailRepo;

    @Autowired
    JWTUtil util;

    @Autowired
    LoggerService loggerService;

    @Autowired
    UserRoleRepo userRoleRepo;

    @Autowired
    ObjectMapper objectMapper;

    Gson gson = new Gson();

    @GetMapping("/get_nameQuery/{account_number}")
    public  Object get_nameQuery(@PathVariable String account_number,HttpServletRequest request) {

        final String name_Query_api = env.getProperty("name_Query") + account_number;
        String token = env.getProperty("wso2Token");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        Users user = getUser(request);
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
                    loggerService.createNewLog(user,request.getRequestURI(),error.toString());
                    return error;
                } else {
                    loggerService.createNewLog(user,request.getRequestURI(),responseBody.toString());
                    return responseBody;
                }
            } else {

                Map<String, Object> error = new HashMap<>();
                error.put("message", "Response body is empty or null");
                error.put("status", false);
                loggerService.createNewLog(user,request.getRequestURI(),error.toString());
                return error;


            }
        } catch (HttpClientErrorException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", ex.getMessage());
            error.put("status", false);
            loggerService.createNewLog(user,request.getRequestURI(),error.toString());
            return error;
            // Handle unauthorized error
        }
    }

    @GetMapping("/hajjList")
    public List<HUjjaj> getAllList(HttpServletRequest request){
        Users user = getUser(request);
        loggerService.createNewLog(user,request.getRequestURI(),convertToStringValues(hujjajRepo.findAll()));
        return hujjajRepo.findAll();
    }

    @GetMapping("/hajjList/paid")
    public List<HUjjaj> getAllPaid(HttpServletRequest request){
        Users user = getUser(request);
        loggerService.createNewLog(user,request.getRequestURI(),convertToStringValues(hujjajRepo.findHUjjajByPaidStatus(true,user.getBranch().getName())));
        List<HUjjaj> paidList = hujjajRepo.findHUjjajByPaidStatus(true,user.getBranch().getName());
        if(userRoleRepo.findByUser(user).get().getRole().getName().toLowerCase().contains("maker")){
            return paidList.stream().filter(hj->hj.getMaker_Id().getId()==user.getId()).collect(Collectors.toList());
        }
        return paidList;
    }

    @GetMapping("/hajjList/unpaid")
    public List<HUjjaj> getAllUnpaid(HttpServletRequest request){
        Users user = getUser(request);
        loggerService.createNewLog(user,request.getRequestURI(),convertToStringValues(hujjajRepo.findHUjjajByPaidStatus(false,user.getBranch().getName())));
        return hujjajRepo.findHUjjajByPaidStatus(false,user.getBranch().getName());
    }

    @GetMapping("/payment_code/{payment_code}")
    public Object getHajjByPaymentCode(@PathVariable String payment_code,HttpServletRequest request){
        Users user = getUser(request);
        Optional<HUjjaj> check = hujjajRepo.findHUjjajByPaymentCode(payment_code,user.getBranch().getName());
        if(check.isEmpty()){
            Map<String, Object> error = new HashMap<>();
            error.put("message", "The Payment Code "+payment_code+" does not exist or You are from different branch");
            error.put("status", "failed");
            loggerService.createNewLog(user,request.getRequestURI(),error.toString());
            return error;
        }
        if(check.get().isPaid()){
            Map<String, Object> success = new HashMap<>();
            success.put("status", "success");
            success.put("data", check.get());
            loggerService.createNewLog(user,request.getRequestURI(),gson.toJson(success));
            return success;
        }
        Map<String, Object> error = new HashMap<>();
        error.put("message", "No Payment has not been made");
        error.put("status", "failed");
        loggerService.createNewLog(user,request.getRequestURI(),error.toString());
        return error;
    }

    @GetMapping("/get_hujaj/{payment_code}")
    public  Object get_hujaj(@PathVariable String payment_code,HttpServletRequest request) throws JsonProcessingException {
        final String apiUrl = env.getProperty("hajjApi");
        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("payment_code", payment_code);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("x-Authorization", env.getProperty("x-auth"));
        headers.set("x-Authorization-Secret", env.getProperty("x-secret"));
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        Users user = getUser(request);
        try {

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET,requestEntity,String.class,uriVariables);
            // Process the response
            loggerService.createNewLog(user,request.getRequestURI(),response.getBody());
           return  Objects.requireNonNull(response.getBody());
        }

        catch (HttpClientErrorException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", ex.getMessage().substring(ex.getMessage().lastIndexOf("error")+8,ex.getMessage().lastIndexOf("}")-1));
            error.put("success", false);
            loggerService.createNewLog(user,request.getRequestURI(),error.toString());
            return error;


        }

    }

    @PreAuthorize("hasRole('maker')")
    @PostMapping("/make_hajj_trans")
    public Object make_hujaj_transaction(@RequestBody HUjjaj hujaj,HttpServletRequest request)

    {
        Timestamp  date= Timestamp.valueOf(LocalDateTime.now());
        hujaj.setCreated_at(date);
        hujaj.setUpdated_at(date);
        double amount;
        double amount_inAccount;
        Users user = getUser(request);
        try {
            amount = Double.parseDouble(hujaj.getAmount());
            amount_inAccount = Double.parseDouble(hujaj.getAmount_inaccount());
        }
        catch(NumberFormatException | NullPointerException e){
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Please Enter a Valid Amount Value");
            error.put("status", "failed");
            loggerService.createNewLog(user,request.getRequestURI(),error.toString());
            return error;
        }
        hujaj.setUSERID(userDetailRepo.findUserDetailByUser(user).get().getFull_name());
        hujaj.setBranch_name(user.getBranch().getName());
        if (amount <= amount_inAccount){
                hujaj.setMaker_Id(user);
                try {
                    Optional<HUjjaj> fetchHujaj = hujjajRepo.findHUjjajByPaymentCode(hujaj.getPayment_code(),user.getBranch().getName());
                    if(fetchHujaj.isEmpty())
                    {
                        hujjajRepo.save(hujaj);
                    }
                    else {
                        Map<String, Object> error = new HashMap<>();
                        error.put("message", "The Payment Code "+hujaj.getPayment_code()+" is already registered");
                        error.put("status", "failed");
                        loggerService.createNewLog(user,request.getRequestURI(),error.toString());
                        return error;
                    }

                }
                catch(Exception e){
                    return  e.getMessage();
                }
                Map<String, Object> success = new HashMap<>();
                success.put("message", "Transaction Made Successfully");
                success.put("status", "Success");
                loggerService.createNewLog(user,request.getRequestURI(),success.toString());
                return success;
        }
        Map<String, Object> error = new HashMap<>();
        error.put("message", "You Don't Have Enough Account Balance To Make the Transaction");
        error.put("status", "failed");
        loggerService.createNewLog(user,request.getRequestURI(),error.toString());
        return error;
    }

    @PreAuthorize("hasRole('checker')")
    @PostMapping("/Check_hajj_trans")
    public  Object CHECK_hujaj_transaction(@RequestBody HUjjaj hUjjaj, HttpServletRequest request)
    {

//   Accept values from user and check the transaction
//        call wso2 end point post transaction

        Users user = getUser(request);

        String amount=hUjjaj.getAmount();
        String  draccount=hUjjaj.getAccount_number();
        String  naration= hUjjaj.getNARRATION();
        String paymentcode=hUjjaj.getPayment_code();

        Optional<HUjjaj> updatedHujaj = hujjajRepo.findHUjjajByPaymentCode(paymentcode,user.getBranch().getName());
        if(updatedHujaj.isEmpty()){
            Map<String, Object> error = new HashMap<>();
            error.put("error", "There is no transaction made with "+paymentcode+" payment code");
            error.put("success", false);
            loggerService.createNewLog(user,request.getRequestURI(),error.toString());
            return error;
        }
        else if(updatedHujaj.get().isPaid()||updatedHujaj.get().is_fundtransfered()){
            Map<String, Object> error = new HashMap<>();
            error.put("error", "The user has already paid");
            error.put("success", false);
            loggerService.createNewLog(user,request.getRequestURI(),error.toString());
            return error;
        }
        else if(!updatedHujaj.get().getBranch_name().equals(user.getBranch().getName())){
            Map<String, Object> error = new HashMap<>();
            error.put("error", "You are not authorized to make check");
            error.put("success", false);
            loggerService.createNewLog(user,request.getRequestURI(),error.toString());
            return error;
        }

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
        Map<String, Object> responseBody;
        try {
            ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {});

            responseBody = responseEntity.getBody();
            if (responseBody != null) {
                Object MSGSTAT = responseBody.get("MSGSTAT");

                if ("FAILURE".equals(MSGSTAT)) {
                    Object EDESC = responseBody.get("EDESC");

                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "Unable To make Fund Transfer. Due to "+ EDESC);
                    error.put("success", false);
                    loggerService.createNewLog(user,request.getRequestURI(),error.toString());
                    return error;
                }
                else
                {

                   HUjjaj update_huj = updateTable(paymentcode,updatedHujaj.get(),responseBody,user);
                    Map<String,Object>  resp = (Map<String, Object>) Post_to_hajserver(update_huj);
                     boolean status = (boolean) resp.get("success");
                     if(status)
                     {
                         update_huj.setPaid(true);
                         hujjajRepo.save(update_huj);
                         Map<String, Object> error = new HashMap<>();
                         error.put("message", "Transaction Authorized Successfully");
                         error.put("success", true);
                         loggerService.createNewLog(user,request.getRequestURI(),error.toString());
                         return error;
                     }

//                   return resp;
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", resp.get("error"));
                    error.put("success", false);
                    loggerService.createNewLog(user,request.getRequestURI(),error.toString());
                    return error;
                }
            }
            else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Response body is empty or null");
                error.put("success", false);
                loggerService.createNewLog(user,request.getRequestURI(),error.toString());
                return error;

            }
        }
        catch (HttpClientErrorException ex) {
            // Handle unauthorized error
            Map<String, Object> error = new HashMap<>();
            error.put("error", ex.getMessage());
            error.put("success", false);
            loggerService.createNewLog(user,request.getRequestURI(),error.toString());
            return error;

        }

    }

    public HUjjaj updateTable(String paymentCode,HUjjaj hujjaj,Map<String, Object> hujajRequest,Users checker){

            hujjaj.set_fundtransfered(true);
            hujjaj.setEXTERNAL_REF_NO(hujajRequest.get("TRANS_REF_NO").toString());
            hujjaj.setTrans_ref_no(hujajRequest.get("FCCREF").toString());
            hujjaj.setAC_BRANCH(hujajRequest.get("AC_BRANCH").toString());
            hujjaj.setNARRATION(hujajRequest.get("NARRATIVE").toString());
            hujjaj.setCUST_NAME(hujajRequest.get("ACCOUNT_HOLDER").toString());
            hujjaj.setAC_NO(hujajRequest.get("ACCOUNT_NUMBER").toString());
            hujjaj.setLCY_AMOUNT(hujajRequest.get("LCY_AMOUNT").toString());
            hujjaj.setRELATED_CUSTOMER(hujajRequest.get("RELATED_CUSTOMER").toString());
            hujjaj.setRELATED_ACCOUNT(hujajRequest.get("RELATED_ACCOUNT").toString());
            hujjaj.setTRN_DT(hujajRequest.get("TRN_DT").toString());
            hujjaj.setVALUE_DT(hujajRequest.get("VALUE_DATE").toString());
            hujjaj.setAUTH_ID(userDetailRepo.findUserDetailByUser(checker).get().getFull_name());
            hujjaj.setSTMT_DT(hujajRequest.get("TRANSACTION_DATE").toString());
//            hujjaj.setNODE(hujajRequest.getNODE());
        //    hujjaj.setAVLDAYS(hujajRequest.getAVLDAYS());

        hujjaj.setAC_CCY(hujajRequest.get("AC_CCY").toString());
            hujjaj.setAUTH_TIMESTAMP(hujajRequest.get("AUTH_TIMESTAMP").toString());
            hujjaj.setChecker_Id(checker);
            hujjajRepo.save(hujjaj);
            return hujjaj;
    }

public  Object Post_to_hajserver(HUjjaj hUjjaj)
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
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
    headers.setContentType(MediaType.APPLICATION_JSON);
    String jsonBody = String.format("""
            {\s
            "payment_code":"%s",
            "paid":true,
            "bank_code": "%s",
            "account_number": "%s",
            "account_holder":"%s",
            "refrence_number":"%s", 
            "date": "%s",
            "amount": %s 
            }
            """,paymentcode,bank_code,account_number,account_holder,refrence_number,date,amount);

    HttpEntity<String> requestEntity = new HttpEntity<>(jsonBody,headers);
    try {
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> responseBody = responseEntity.getBody();
        if (responseBody != null) {
            return  responseBody;
        }
        else {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Response body is empty or null");
            error.put("success", false);
            return error;
        }
    }
    catch (HttpClientErrorException ex) {
        // Handle unauthorized error
        Map<String, Object> error = new HashMap<>();
        error.put("error", ex.getMessage().substring(ex.getMessage().lastIndexOf("error")+8,ex.getMessage().lastIndexOf("}")-1));
        error.put("success", false);
        return error;
    }

}
 Users getUser(HttpServletRequest request){
     String jwtToken = request.getHeader("Authorization");
     String username = util.validateTokenAndRetrieveSubject(jwtToken.split(" ")[1]);
     return usersRepo.findUsersByUsername(username).get();
 }

 String convertToStringValues(List values) {
    StringJoiner joiner = new StringJoiner(", ", "[", "]");
    for (Object element : values) {
            try {
                joiner.add(objectMapper.writeValueAsString(element));
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
    }
    return joiner.toString();
 }

}
