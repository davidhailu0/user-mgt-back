package com.hajj.hajj.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    boolean is_fundtransfered;
    String account_number;
    String account_holder;
    @JsonProperty("trans_ref_no")
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
    @JsonIgnore
    Users Maker_Id ;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "Checker_Id",referencedColumnName = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnore
    Users Checker_Id;

    Timestamp created_at;
    Timestamp updated_at;
    @JsonProperty("isFromMobile")
    boolean isFromMobile;

    public HUjjaj(){

    }
}
