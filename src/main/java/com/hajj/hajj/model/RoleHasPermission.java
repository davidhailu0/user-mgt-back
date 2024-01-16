package com.hajj.hajj.model;


import java.sql.Timestamp;
import java.util.List;

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
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rolehas_seq")
    // @SequenceGenerator(sequenceName = "rolehas_seq", allocationSize = 1, name = "rolehas_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name="role_id",referencedColumnName = "id")
    Role role;
    @OneToMany(cascade = CascadeType.REMOVE)
    List<Permission> permissions;
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

    public RoleHasPermission(Role role, List<Permission> permissions, Users created_by, Users updated_by, Timestamp created_at, Timestamp updated_at, String status) {
        this.role = role;
        this.permissions = permissions;
        this.created_by = created_by;
        this.updated_by = updated_by;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.status = status;
    }
}

