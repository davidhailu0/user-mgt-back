package com.hajj.hajj.DTO;

import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class UserRoleRequest{
    Long id;
    Long role;
    Long user;
    Long assigned_by;
    Long updated_by;
    String status;

    public UserRoleRequest(){

    }
}
