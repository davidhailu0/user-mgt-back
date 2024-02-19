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
public class UserUpdate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String previousName;
    String newName;
    String previousPhoneNumber;
    String newPhoneNumber;
    @ManyToOne
    Users user;
    @ManyToOne
    Branch previousBranch;
    @ManyToOne
    Branch newBranch;
    @ManyToOne
    Role previousRole;
    @ManyToOne
    Role newRole;
    @ManyToOne
    Users created_by;
    String previousStatus;
    String newStatus;
    boolean previousAccountLockStatus;
    boolean newAccountLockStatus;
    Timestamp created_at;

    public String toJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
