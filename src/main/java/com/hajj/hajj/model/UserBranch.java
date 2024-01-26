package com.hajj.hajj.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class UserBranch {
    @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userrole_seq")
    // @SequenceGenerator(sequenceName = "userrole_seq", allocationSize = 1, name = "userrole_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name="branch_id",referencedColumnName = "id")
    Branch branch;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name="user_id",referencedColumnName = "id")
    Users user;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "assigned_by_id",referencedColumnName = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnore
    Users assigned_by;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "updated_by_id",referencedColumnName = "id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonIgnore
    Users updated_by;
    Timestamp created_at;
    Timestamp updated_at;
    String status;

    public UserBranch(){

    }
}
