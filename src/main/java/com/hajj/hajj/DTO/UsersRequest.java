package com.hajj.hajj.DTO;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UsersRequest{
    
    Long id;
    String username;
    String fullname;
    String salt;
    String password;
    Long branch;
    Long created_by;
    Long updated_by;
    String status;

    public UsersRequest(){

    }

    public UsersRequest(String username, String fullname,String salt, String password, Long branch, Long created_by, Long updated_by, String status) {
        this.username = username;
        this.salt = salt;
        this.fullname = fullname;
        this.password = password;
        this.branch = branch;
        this.created_by = created_by;
        this.updated_by = updated_by;
        this.status = status;
    }
}