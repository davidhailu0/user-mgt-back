package com.hajj.hajj.model;


import java.sql.Timestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class RoleHasPermission{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rolehas_seq")
    @SequenceGenerator(sequenceName = "rolehas_seq", allocationSize = 1, name = "rolehas_seq")
    Long id;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name="role_id",referencedColumnName = "id")
    Role role;
    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name="permission_id",referencedColumnName = "id")
    Permission[] permission;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "created_by_id",referencedColumnName = "id")
    Users created_by;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "updated_by_id",referencedColumnName = "id")
    Users updated_by;
    Timestamp created_at;
    Timestamp updated_at;
    String status;

    public RoleHasPermission(){

    }
}

