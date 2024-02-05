package com.hajj.hajj.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserBranchRequest {
    Long id;
    Long branch;
    Long user;
    Long assigned_by;
    Long updated_by;
    String status;

    public UserBranchRequest(){

    }
}
