package com.SSproject.classes.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="user")
public class UserPojo {

    private Long id;
    @NotBlank(message = "Username non può essere vuoto")
    private String username;

    @NotBlank(message = "Password non può essere vuota")
    @Size(min = 8, message = "La password deve essere lunga almeno 8 caratteri")
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
