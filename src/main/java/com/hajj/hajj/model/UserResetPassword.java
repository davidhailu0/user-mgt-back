package com.hajj.hajj.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class UserResetPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @ManyToOne
    Users reset_user;
    @ManyToOne
    Users maker;
    @ManyToOne
    Users checker;

    Timestamp created_at;
    Timestamp updated_at;

    public String toJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
