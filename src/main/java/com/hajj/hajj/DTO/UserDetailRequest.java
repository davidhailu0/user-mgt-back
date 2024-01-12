package com.hajj.hajj.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetailRequest{
    String full_name;
    Long created_by;
    Long updated_by;
    String status;

    public UserDetailRequest(){

    }
}
