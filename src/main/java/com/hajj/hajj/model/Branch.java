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
    private Users created_by;
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "updated_by_id",referencedColumnName = "id")
    private Users updated_by;
    private Timestamp created_at;
    private Timestamp updated_at;
    private String status;

    public Branch(){

    }
}
