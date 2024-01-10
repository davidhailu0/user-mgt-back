package com.hajj.hajj.model;

import java.sql.Timestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Users{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(sequenceName = "user_seq", allocationSize = 1, name = "user_seq")
    Long id;
    @NotBlank String username;
    String salt;
    @NotBlank
    String password;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "branch_id",referencedColumnName = "id")
    Branch branch;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "created_by_id",referencedColumnName = "id")
    Users created_by;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "updated_by_id",referencedColumnName = "id")
    Users updated_by;
    Timestamp created_at;
    Timestamp updated_at;
    String status;

    public Users(){

    }

    public Users(String username, String salt, String password, Branch branch, Users created_by, Users updated_by, Timestamp created_at, Timestamp updated_at, String status) {
        this.username = username;
        this.salt = salt;
        this.password = password;
        this.branch = branch;
        this.created_by = created_by;
        this.updated_by = updated_by;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.status = status;
    }
}