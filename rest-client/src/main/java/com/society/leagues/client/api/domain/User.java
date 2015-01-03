package com.society.leagues.client.api.domain;

import com.society.leagues.client.api.Role;

import javax.annotation.security.DenyAll;
import java.util.*;

@SuppressWarnings("unused")
public class User {
    String username;
    String firstName;
    String lastName;
    String email;
    String password;
    String login;
    int id;
    Set<Role> roles = new TreeSet<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DenyAll
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        roles.addAll(roles);
    }

    public void setRole(Role role) {
        roles.add(role);
    }

     public void addRole(Role role) {
        roles.add(role);
    }

    public boolean isAdmin() {
        for (Role role : roles) {
            if (role == Role.ADMIN || role == Role.OPERATOR)
                return true;
        }
        return false;
    }

}
