package com.hajj.hajj.DTO;

import com.hajj.hajj.model.Branch;
import com.hajj.hajj.model.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginResponse {
    String username;
    Branch branch;
    Role role;
    String status;
    String token;
}
