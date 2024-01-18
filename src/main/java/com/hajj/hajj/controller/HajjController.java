package com.hajj.hajj.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hajj.hajj.config.JWTUtil;
import com.hajj.hajj.model.HUjjaj;
import com.hajj.hajj.model.Users;
import com.hajj.hajj.repository.HujjajRepo;
import com.hajj.hajj.repository.UserDetailRepo;
import com.hajj.hajj.repository.UsersRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    public List<HUjjaj> getAllPaid(HttpServletRequest request){
        String jwtToken = request.getHeader("Authorization");
        String username = util.validateTokenAndRetrieveSubject(jwtToken.split(" ")[1]);
        Users user = usersRepo.findUsersByUsername(username).get();
        return hujjajRepo.findHUjjajByPaidStatus(true,user.getBranch().getName());
    }

    @GetMapping("/hajjList/unpaid")
    public List<HUjjaj> getAllUnpaid(HttpServletRequest request){
        String jwtToken = request.getHeader("Authorization");
        String username = util.validateTokenAndRetrieveSubject(jwtToken.split(" ")[1]);
        Users user = usersRepo.findUsersByUsername(username).get();
        return hujjajRepo.findHUjjajByPaidStatus(false,user.getBranch().getName());
    }

    @GetMapping("/payment_code/{payment_code}")
    public Object getHajjByPaymentCode(@PathVariable String payment_code, HttpServletResponse resp,HttpServletRequest request){
        String jwtToken = request.getHeader("Authorization");
        String username = util.validateTokenAndRetrieveSubject(jwtToken.split(" ")[1]);
        Users user = usersRepo.findUsersByUsername(username).get();
        Optional<HUjjaj> check = hujjajRepo.findHUjjajByPaymentCode(payment_code,user.getBranch().getName());
        if(check.isEmpty()){
            Map<String, Object> error = new HashMap<>();
            error.put("message", "The Payment Code "+payment_code+" does not exist or You are from different branch");
            error.put("status", "failed");
            return error;
        }
        if(check.get().isPaid()){
            Map<String, Object> success = new HashMap<>();
            success.put("status", "success");
            success.put("data", check.get());
            return success;
        }
        Map<String, Object> error = new HashMap<>();
        error.put("message", "No Payment has not been made");
        error.put("status", "failed");
        return error;
    }

    @GetMapping("/get_hujaj/{payment_code}")
    public  Object get_hujaj(@PathVariable String payment_code) throws JsonProcessingException {
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
        try {

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET,requestEntity,String.class,uriVariables);
            // Process the response
           return  Objects.requireNonNull(response.getBody());
        }

        catch (HttpClientErrorException ex) {
            System.out.println(ex.getMessage());
            return ex.getMessage();

        }

    }

    @PreAuthorize("hasRole('maker')")
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
    public  Object CHECK_hujaj_transaction(@RequestBody HUjjaj hUjjaj, HttpServletRequest request)
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

        Optional<HUjjaj> updatedHujaj = hujjajRepo.findHUjjajByPaymentCode(paymentcode,user.getBranch().getName());
        if(updatedHujaj.isEmpty()){
            Map<String, Object> error = new HashMap<>();
            error.put("error", "There is no transaction made with "+paymentcode+" payment code");
            error.put("success", false);
            return error;
        }
        else if(!updatedHujaj.get().getBranch_name().equals(user.getBranch().getName())){
            Map<String, Object> error = new HashMap<>();
            error.put("error", "You are not authorized to make check from different branch");
            error.put("success", false);
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
                    return error;
                }
                else
                {
                   HUjjaj update_huj = updateTable(paymentcode,responseBody,updatedHujaj.get(),user);
                   Object resp = Post_to_hajserver(update_huj);
                   update_huj.setPaid(true);
                   hujjajRepo.save(update_huj);
                   return resp;
                }
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
            error.put("error", ex.getMessage());
            error.put("success", false);
            return error;

        }

    }

    public HUjjaj updateTable(String paymentCode,Map<String, Object> hujajRequest,HUjjaj updatedHujjaj,Users checker){
        updatedHujjaj.setEXTERNAL_REF_NO(hujajRequest.get("FCCREF").toString());
        updatedHujjaj.setTrans_ref_no(hujajRequest.get("FCCREF").toString());
        updatedHujjaj.setAC_BRANCH(hujajRequest.get("ACCOUNT_BRANCH").toString());
//            hujjaj.setBranch_name(hujajRequest.getBranch_name());
//            hujjaj.setNARRATION(hujajRequest.getNARRATION());
//            hujjaj.setCUST_NAME(hujajRequest.getCUST_NAME());
//            hujjaj.setTRN_REF_NO(hujajRequest.getTRN_REF_NO());
//            hujjaj.setAC_NO(hujajRequest.getAC_NO());
//            hujjaj.setLCY_AMOUNT(hujajRequest.getLCY_AMOUNT());
//            hujjaj.setRELATED_CUSTOMER(hujajRequest.getRELATED_CUSTOMER());
//            hujjaj.setRELATED_ACCOUNT(hujajRequest.getRELATED_ACCOUNT());
//            hujjaj.setTRN_DT(hujajRequest.getTRN_DT());
//            hujjaj.setVALUE_DT(hujajRequest.getVALUE_DT());
//            hujjaj.setAVLDAYS(hujajRequest.getAVLDAYS());
        updatedHujjaj.setAUTH_ID(userDetailRepo.findUserDetailByUser(checker).get().getFull_name());
        updatedHujjaj.setSTMT_DT(hujajRequest.get("TRANSACTION_DATE").toString());
//            hujjaj.setNODE(hujajRequest.getNODE());
//            hujjaj.setAC_CCY(hujajRequest.getAC_CCY());
        updatedHujjaj.setAUTH_TIMESTAMP(LocalDateTime.now().toString());
        updatedHujjaj.setChecker_Id(checker);
            hujjajRepo.save(updatedHujjaj);
            return updatedHujjaj;
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
    String jsonBody = "{ " +
            "\"payment_code\":" +"\""+ paymentcode + "\"" + ","+
            "\"paid\":" +"\""+ true + "\"" + ","+
            "\"bank_code\": " +"\""+ bank_code + "\"" + ","+
            "\"account_number\": " +"\""+ account_number + "\"" + ","+
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
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Response body is empty or null");
            error.put("success", false);
            return error;
        }
    }
    catch (HttpClientErrorException ex) {
        // Handle unauthorized error
        Map<String, Object> error = new HashMap<>();
        error.put("error", ex.getMessage());
        error.put("success", false);
        return error;
    }

}

}
