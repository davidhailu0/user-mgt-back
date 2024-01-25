package com.hajj.hajj.model;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Permission{
    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "perm_seq")
    // @SequenceGenerator(sequenceName = "perm_seq", allocationSize = 1, name = "perm_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "created_by_id",referencedColumnName = "id")
    Users created_by;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "updated_by_id",referencedColumnName = "id")
    Users updated_by;
    Timestamp created_at;
    @ManyToMany(mappedBy = "permissionList")
    @JsonIgnore
    List<Role> roleList;
    Timestamp updated_at;
    String status;

    public Permission(){

    }

    public Permission(String name, Users created_by, Users updated_by, Timestamp created_at, Timestamp updated_at, String status) {
        this.name = name;
        this.created_by = created_by;
        this.updated_by = updated_by;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.status = status;
    }
}
