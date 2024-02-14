package com.hajj.hajj.model;

import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserDetail{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Date start_date;
    Date status_changed_on;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name="user_id",referencedColumnName = "id")
    Users user;
    @NotBlank String full_name;
    @Column(unique = true)
    String phoneNumber;
    @JsonManagedReference
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "created_by_id",referencedColumnName = "id")
    Users created_by;
    @JsonManagedReference
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "updated_by_id",referencedColumnName = "id")
    Users updated_by;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "checker_id",referencedColumnName = "id")
    Users checker;
    Timestamp created_at;
    Timestamp updated_at;
    String status;

    public UserDetail(){

    }

    public UserDetail(Date start_date, Date status_changed_on, Users user,String full_name, Users created_by, Users updated_by, Timestamp created_at, Timestamp updated_at, String status) {
        this.start_date = start_date;
        this.status_changed_on = status_changed_on;
        this.user = user;
        this.full_name = full_name;
        this.created_by = created_by;
        this.updated_by = updated_by;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.status = status;
    }

    public String toJson() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }
}
