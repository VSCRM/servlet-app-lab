package com.example.myapp.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "users")
@NamedQueries({
        @NamedQuery(name = "com.example.myapp.core.User.findAll", query = "SELECT u FROM User u")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private long id;

    @NotBlank
    @Column(name = "username")
    @JsonProperty
    private String name;

    @Email
    @Column(name = "email")
    @JsonProperty
    private String email;

    @Column(name = "password")
    @JsonProperty
    private String password = "default_password";

    public User() {}

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}