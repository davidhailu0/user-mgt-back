package com.hajj.hajj.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String sender;
    String receiver;
    String net_content;
    String content;
    String message_type;
    Timestamp created_at;
    Timestamp updated_at;
    boolean messageStatus;
}
