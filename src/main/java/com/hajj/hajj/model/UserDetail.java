package com.hajj.hajj.model;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserDetail{
    @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userdetail_seq")
    // @SequenceGenerator(sequenceName = "userdetail_seq", allocationSize = 1, name = "userdetail_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Date start_date;
    Date status_changed_on;
    @NotBlank String full_name;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "created_by_id",referencedColumnName = "id")
    Users created_by;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "updated_by_id",referencedColumnName = "id")
    Users updated_by;
    Timestamp created_at;
    Timestamp updated_at;
    String status;

    public UserDetail(){

    }
}
