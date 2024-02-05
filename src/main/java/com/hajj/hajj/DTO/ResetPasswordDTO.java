package com.hajj.hajj.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDTO {
    String previousPassword;
    String newPassword;
}
