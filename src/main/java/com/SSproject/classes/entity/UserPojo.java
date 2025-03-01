package com.SSproject.classes.entity;

import jakarta.persistence.*;

@Entity
@Table(name="user")
public class UserPojo {

    private Long id;
    private String username;
    private String password;

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
