package com.hajj.hajj.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.hajj.hajj.DTO.HajjQueryDTO;
import com.hajj.hajj.config.JWTUtil;
import com.hajj.hajj.model.HUjjaj;
import com.hajj.hajj.model.UserRole;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    ObjectMapper objectMapper;

    @Autowired
    Gson gson;

    String token;


    @Scheduled(fixedRate = 1000*60*60*24)
    public void refreshToken(){
        RestTemplate restTemplate=new RestTemplate();
        HttpHeaders headers =  new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", env.getProperty("grant_type"));
        map.add("client_id", env.getProperty("client_id"));
        map.add("client_secret", env.getProperty("client_secret"));
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(map,headers);
        ResponseEntity<Map<String, Object>> responseEntity = restTemplate.exchange(Objects.requireNonNull(env.getProperty("wso2TokenURL")), HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Map<String, Object>>() {});
        Map<String, Object> responseBody = responseEntity.getBody();
        token = (String) responseBody.get("access_token");
    }


    @GetMapping("/getHajjData")
    public Object getHajjData(HttpServletRequest request){
        Users user = getUser(request);
        List<HUjjaj> hujajData = hujjajRepo.getDashboardData(user.getBranch().getName());
        int paid = hujajData.stream().filter(HUjjaj::isPaid).toList().size();
        int unpaid = hujajData.stream().filter(hj->!hj.isPaid()).toList().size();
        int total = hujajData.size();
        Map<String,Integer> hajjData = new HashMap<>();
        hajjData.put("total",total);
        hajjData.put("unpaid",unpaid);
        hajjData.put("paid",paid);
        hajjData.put("mobile",0);
        loggerService.createNewLog(user,request.getRequestURI(),hajjData.toString());
        return hajjData;
    }

    @PostMapping("/filteredHajjReport")
    public List<HUjjaj> filteredData(@RequestBody HajjQueryDTO hajjQueryDTO){
        if(hajjQueryDTO.getStatus()==null&&hajjQueryDTO.getFromDate()==null&&hajjQueryDTO.getToDate()==null&&hajjQueryDTO.getBranchName()==null){
            return List.of();
        }
        List<HUjjaj> allHajj;
        if(hajjQueryDTO.getBranchName()!=null&&!hajjQueryDTO.getBranchName().equals("null")){
            allHajj = hujjajRepo.getDashboardData(hajjQueryDTO.getBranchName());
        }
        else{
            allHajj = hujjajRepo.findAll();
        }
        allHajj = allHajj.stream().filter((hj)->{
            String pattern = "yyyy-MM-dd";
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDate trnDate = LocalDate.parse(hj.getTRN_DT(), formatter);
            LocalDate fromDate = null;
            LocalDate toDate = null;
            if(hajjQueryDTO.getFromDate()!=null&&!hajjQueryDTO.getFromDate().equals("null")&& !hajjQueryDTO.getFromDate().isEmpty()){
                fromDate = LocalDate.parse(hajjQueryDTO.getFromDate(), formatter);
                if(trnDate.isBefore(fromDate)){
                    return false;
                }
            }
            if(hajjQueryDTO.getToDate()!=null&& !hajjQueryDTO.getToDate().equals("null")&&!hajjQueryDTO.getToDate().isEmpty()){
                toDate = LocalDate.parse(hajjQueryDTO.getToDate(), formatter);
                if(trnDate.isAfter(toDate)){
                    return false;
                }
            }
            if(hajjQueryDTO.getStatus().equals("paid")){
                if(!hj.isPaid()){
                    return false;
                }
            }
            else if(hajjQueryDTO.getStatus().equals("unpaid")){
                if(hj.isPaid()){
                    return false;
                }
            }
            return true;
        }).toList();
        return allHajj;
    }

    @GetMapping("/get_nameQuery/{account_number}")
    public  Object get_nameQuery(@PathVariable String account_number,HttpServletRequest request) {

        final String name_Query_api = env.getProperty("name_Query") + account_number;
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
        if(user.getRole().getName().toLowerCase().contains("maker")){
            return paidList.stream().filter(hj-> Objects.equals(hj.getMaker_Id().getId(), user.getId())).collect(Collectors.toList());
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
            loggerService.createNewLog(user,request.getRequestURI(),success.toString());
            return success;
        }
        Map<String, Object> error = new HashMap<>();
        error.put("message", "Please Make Sure the User has Paid");
        error.put("status", "failed");
        //loggerService.createNewLog(user,request.getRequestURI(),error.toString());
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


    @GetMapping("/hajjListByBranch")
    public List<HUjjaj> getMakerSpecificList(HttpServletRequest request){
        Users user = getUser(request);
        if(user.getRole().getName().contains("maker")){
            return hujjajRepo.getMadeHujjajList(user,user.getBranch().getName());
        }
        return hujjajRepo.getCheckedHujjajList(user.getBranch().getName());
    }

    @PreAuthorize("hasAnyRole('maker','superadmin')")
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

    @GetMapping("/unauthorizedTransactions")
    public List<HUjjaj> getUnauthorizedTransactions(HttpServletRequest request){
        Users user = getUser(request);
        return hujjajRepo.getUnauthorizedTransactions(user.getBranch().getName());
    }

    @PreAuthorize("hasRole('superadmin')")
    @GetMapping("/authorizeRequest/{paymentCode}")
    public Object authorizeRequest(HttpServletRequest request,@PathVariable String paymentCode){
        Users user = getUser(request);
        Optional<HUjjaj> unauthorizedRequest = hujjajRepo.findHUjjajByPaymentCode(paymentCode,user.getBranch().getName());
        if(unauthorizedRequest.isEmpty()){
            Map<String,Object> error= new HashMap<String,Object>();
            error.put("success",false);
            error.put("error",String.format("The Payment Code %s is not registered",paymentCode));
            return error;
        }
        if (unauthorizedRequest.get().isPaid()) {
            Map<String,Object> error= new HashMap<String,Object>();
            error.put("success",false);
            error.put("error","The Payment Code has already been used");
            return error;
        }
        HUjjaj update_huj = unauthorizedRequest.get();
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
        Map<String,Object> error= new HashMap<String,Object>();
        error.put("success",false);
        error.put("error",resp.get("error"));
        return error;
    }

    @PreAuthorize("hasAnyRole('checker','superadmin')")
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
            //loggerService.createNewLog(user,request.getRequestURI(),error.toString());
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

                   HUjjaj update_huj = updateTable(updatedHujaj.get(),responseBody,user);
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

    public HUjjaj updateTable(HUjjaj hujjaj, Map<String, Object> hujajRequest, Users checker){

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
