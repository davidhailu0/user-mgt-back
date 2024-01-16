package com.hajj.hajj.DTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class ResponseDTO {
    boolean success;
    String error;

    public ResponseDTO(){

    }
    public ResponseDTO(boolean success,String error){
        this.success = success;
        this.error = error;
    }
}
