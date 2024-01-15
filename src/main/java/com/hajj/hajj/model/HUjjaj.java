package com.hajj.hajj.model;

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
    Users Maker_Id ;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "Checker_Id",referencedColumnName = "id")
    Users Checker_Id;

    Timestamp created_at;
    Timestamp updated_at;





}
