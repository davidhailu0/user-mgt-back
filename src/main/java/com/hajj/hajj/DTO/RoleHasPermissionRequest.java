package com.hajj.hajj.DTO;


import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RoleHasPermissionRequest{
    Long id;
    Long role;
    Long[] permission;
    Long created_by;
    Long updated_by;
    String status;

    public RoleHasPermissionRequest(){

    }
}

