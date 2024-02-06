package com.hajj.hajj.DTO;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionRequest{
    Long id;
    String name;
    Long created_by;
    Long updated_by;
    String status;

    public PermissionRequest(){

    }

    public PermissionRequest(String name, Long created_by, Long updated_by, Timestamp created_at, Timestamp updated_at, String status) {
        this.name = name;
        this.created_by = created_by;
        this.updated_by = updated_by;
        this.status = status;
    }
}
