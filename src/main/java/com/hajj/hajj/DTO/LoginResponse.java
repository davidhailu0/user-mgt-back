package com.hajj.hajj.DTO;

import com.hajj.hajj.model.Branch;
import com.hajj.hajj.model.Role;
import com.hajj.hajj.model.UserRole;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    String username;
    Branch branch;
    Role role;
    String status;
    String token;

    @Override
    public String toString(){
        return String.format("""
                "user":{
                    "username":%s,
                    "branch":%s,
                    "role":%s,
                    "status":%s
                },
                "token":%s
                }
                """,username,branch==null?"null":branch,role==null?"null":role,status,token);
    }

    public void setRole(UserRole userRole) {
        if(userRole.getRole()!=null){
            role = userRole.getRole();
        }
    }

    public void setBranch(Branch branch2) {
        if(branch2!=null){
            this.branch = branch2;
        }
    }
}
