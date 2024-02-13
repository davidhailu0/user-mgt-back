package com.hajj.hajj.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class UserResetPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne
    Message message;
    @ManyToOne
    Users reset_user;
    @ManyToOne
    Users maker;
    @ManyToOne
    Users checker;

    Timestamp created_at;
    Timestamp updated_at;
}
