package com.hajj.hajj.model;

import java.sql.*;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Role{
    @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_seq")
    // @SequenceGenerator(sequenceName = "role_seq", allocationSize = 1, name = "role_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String description;
    String status;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "created_by_id",referencedColumnName = "id")
    Users created_by;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "updated_by_id",referencedColumnName = "id")
    Users updated_by;
    Timestamp created_at;
    Timestamp updated_at;

    public Role(){

    }

    public Role(String name, String description, String status, Users created_by, Users updated_by, Timestamp created_at, Timestamp updated_at) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.created_by = created_by;
        this.updated_by = updated_by;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }
}
