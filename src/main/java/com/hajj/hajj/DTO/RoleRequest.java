package com.hajj.hajj.DTO;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoleRequest{
    Long id;
    String name;
    String description;
    String status;
    Long created_by;
    Long updated_by;

    public RoleRequest(){

    }
}
