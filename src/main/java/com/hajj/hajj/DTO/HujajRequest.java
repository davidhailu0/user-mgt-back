package com.hajj.hajj.DTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HujajRequest {
    Long id;
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
    Long Maker_Id ;
    Long Checker_Id;
}
