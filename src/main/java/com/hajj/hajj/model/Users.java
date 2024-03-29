package com.hajj.hajj.model;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

import jakarta.persistence.*;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Users implements UserDetails{
    @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    // @SequenceGenerator(sequenceName = "user_seq", allocationSize = 1, name = "user_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Column(unique = true)
    @NotBlank String username;
    String salt;
    @NotBlank
    @JsonProperty(access = Access.WRITE_ONLY)
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

    private List<GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}