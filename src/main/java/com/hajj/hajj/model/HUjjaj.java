package com.hajj.hajj.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Table(name = "hujaj_list")
public class HUjjaj {

    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private  Long id;
    String  first_name;
    String last_name;
    String  middle_name;
    String phone;
    String  photo_url;
    String passport_number;
    String  birth_date;
    String service_package;
    @Column(unique = true)
    String  payment_code;
    String amount;
    boolean paid ;
    String account_number;
    String account_holder;
    String trans_ref_no;
    String amount_inaccount;
    String EXTERNAL_REF_NO;
    String  TRN_CODE ;
    String AC_BRANCH;
    String  branch_name;
    String NARRATION;
    String  CUST_NAME;
    String TRN_REF_NO;
    String  AC_NO;
    String LCY_AMOUNT;
    String  RELATED_CUSTOMER;
    String RELATED_ACCOUNT;
    String TRN_DT ;
    String VALUE_DT;
    String USERID;
    String AVLDAYS;
    String AUTH_ID;
    String STMT_DT;
    String NODE;
    String AC_CCY ;
    String AUTH_TIMESTAMP;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "Maker_Id",referencedColumnName = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Users Maker_Id ;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "Checker_Id",referencedColumnName = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    Users Checker_Id;

    Timestamp created_at;
    Timestamp updated_at;


    public HUjjaj(){

    }
 public HUjjaj(String first_name, String last_name, String middle_name, String phone, String photo_url, String passport_number, String birth_date, String service_package, String payment_code, String amount, boolean paid, String account_number, String account_holder, String trans_ref_no, String amount_inaccount, String EXTERNAL_REF_NO, String TRN_CODE, String AC_BRANCH, String branch_name, String NARRATION, String CUST_NAME, String TRN_REF_NO, String AC_NO, String LCY_AMOUNT, String RELATED_CUSTOMER, String RELATED_ACCOUNT, String TRN_DT, String VALUE_DT, String USERID, String AVLDAYS, String AUTH_ID, String STMT_DT, String NODE, String AC_CCY, String AUTH_TIMESTAMP, Users maker_Id, Users checker_Id) {
  this.first_name = first_name;
  this.last_name = last_name;
  this.middle_name = middle_name;
  this.phone = phone;
  this.photo_url = photo_url;
  this.passport_number = passport_number;
  this.birth_date = birth_date;
  this.service_package = service_package;
  this.payment_code = payment_code;
  this.amount = amount;
  this.paid = paid;
  this.account_number = account_number;
  this.account_holder = account_holder;
  this.trans_ref_no = trans_ref_no;
  this.amount_inaccount = amount_inaccount;
  this.EXTERNAL_REF_NO = EXTERNAL_REF_NO;
  this.TRN_CODE = TRN_CODE;
  this.AC_BRANCH = AC_BRANCH;
  this.branch_name = branch_name;
  this.NARRATION = NARRATION;
  this.CUST_NAME = CUST_NAME;
  this.TRN_REF_NO = TRN_REF_NO;
  this.AC_NO = AC_NO;
  this.LCY_AMOUNT = LCY_AMOUNT;
  this.RELATED_CUSTOMER = RELATED_CUSTOMER;
  this.RELATED_ACCOUNT = RELATED_ACCOUNT;
  this.TRN_DT = TRN_DT;
  this.VALUE_DT = VALUE_DT;
  this.USERID = USERID;
  this.AVLDAYS = AVLDAYS;
  this.AUTH_ID = AUTH_ID;
  this.STMT_DT = STMT_DT;
  this.NODE = NODE;
  this.AC_CCY = AC_CCY;
  this.AUTH_TIMESTAMP = AUTH_TIMESTAMP;
  Maker_Id = maker_Id;
  Checker_Id = checker_Id;
 }
}
