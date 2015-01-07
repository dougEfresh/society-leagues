package com.society.leagues.client.api.domain;

import com.society.leagues.client.api.Role;

import javax.annotation.security.DenyAll;
import javax.validation.constraints.NotNull;

@SuppressWarnings("unused")
public class User extends LeagueObject {

    @NotNull
    protected String firstName;
    @NotNull
    protected String lastName;
    protected String email;
    protected String password;
    @NotNull
    protected String login;
    @NotNull
    protected Role role;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public User() {

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void addRole(Role role) {
         this.role = role;
    }

    public boolean isAdmin() {
        return Role.isAdmin(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (email != null ? !email.equals(user.email) : user.email != null)
            return false;

        return !(login != null ? !login.equals(user.login) : user.login != null);
    }

    @Override
    public int hashCode() {

        int result = 31 * (email != null ? email.hashCode() : 0);
        result = 31 * result + (login != null ? login.hashCode() : 0);
        return result;
    }

    public boolean verify() {
        return email != null &&
                login != null &&
                firstName != null &&
                lastName != null &&
                password != null;
    }

    @Override
    public String toString() {
        return "User{" +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", id=" + id +
                ", role" +
                '}';
    }
}
