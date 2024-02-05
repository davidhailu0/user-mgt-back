package com.hajj.hajj.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchRequest{
    private Long id;
    private String branch_code;
    private String name;
    private Long created_by;
    private Long updated_by;
    private String status;

}
