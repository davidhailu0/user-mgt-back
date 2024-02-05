package com.hajj.hajj.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@Entity
public class Logger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    Users user;

    String request;

    @Column(columnDefinition = "TEXT")
    String response;

    Timestamp created_at;

    public Logger(){

    }

    public Logger(Users user, String request, String response, Timestamp created_at) {
        this.user = user;
        this.request = request;
        this.response = response;
        this.created_at = created_at;
    }
}
