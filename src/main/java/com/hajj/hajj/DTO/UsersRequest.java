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
    boolean accountLocked;

    public UsersRequest(){

    }

    public UsersRequest(String username, String fullname,String phoneNumber,Long branch, Long role, boolean accountLocked) {
        this.username = username;
        this.fullname = fullname;
        this.phoneNumber = phoneNumber;
        this.branch = branch;
        this.role = role;
        this.accountLocked = accountLocked;
    }
}