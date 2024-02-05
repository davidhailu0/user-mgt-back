package com.hajj.hajj.model;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Getter
@Setter
public class Branch{
    @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "branch_seq")
    // @SequenceGenerator(sequenceName = "branch_seq", allocationSize = 1, name = "branch_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String branch_code;
    private String name;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "created_by_id",referencedColumnName = "id")
    @JsonIgnore
    private Users created_by;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "updated_by_id",referencedColumnName = "id")
    @JsonIgnore
    private Users updated_by;
    private Timestamp created_at;
    private Timestamp updated_at;
    private String status;

    public Branch(){

    }

    public Branch(String branch_code, String name, Users created_by, Users updated_by, Timestamp created_at, Timestamp updated_at, String status) {
        this.branch_code = branch_code;
        this.name = name;
        this.created_by = created_by;
        this.updated_by = updated_by;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.status = status;
    }
}
