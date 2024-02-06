package com.hajj.hajj.DTO;

import com.hajj.hajj.model.Branch;
import com.hajj.hajj.model.Role;
import com.hajj.hajj.model.UserRole;
import com.hajj.hajj.model.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    Users user;
    String token;
}

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
class LoggedInUser{
    Long id;
    String username;
    Branch branch;
    Role role;
    String status;
}
