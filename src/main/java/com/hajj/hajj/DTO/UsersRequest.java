package com.hajj.hajj.DTO;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UsersRequest{
    
    Long id;
    String username;
    String fullname;
    String phoneNumber;
    @Nullable
    String password;
    Long branch;
    Long role;
    String status;

    public UsersRequest(){

    }

    public UsersRequest(String username, String fullname,String phoneNumber,String salt, String password, Long branch, Long created_by, Long role,Long updated_by, String status) {
        this.username = username;
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.branch = branch;
        this.role = role;
        this.status = status;
    }
}