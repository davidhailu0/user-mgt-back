package com.hajj.hajj.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
}
