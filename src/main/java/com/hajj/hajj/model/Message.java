package com.hajj.hajj.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String sender;
    @ManyToOne
    UserDetail receiver;
    String net_content;
    @JsonIgnore
    String content;
    String message_type;
    @ManyToOne
    Users createdBy;
    @ManyToOne
    Users checkedBy;
    Timestamp created_at;
    Timestamp updated_at;
    boolean messageStatus;

    public String toJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
