package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.client.api.domain.converters.DateTimeDeSerializer;
import com.society.leagues.client.api.domain.converters.DateTimeSerializer;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User extends LeagueObject {

    @NotNull String firstName;
    @NotNull String lastName;
    String email;
    String password;
    @NotNull String login;
    @NotNull Role role;
    Status status;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime created;
    Set<HandicapSeason> handicapSeasons = new HashSet<>();

    public User() {
        this.created = LocalDateTime.now();
    }
    public User(String id) {this.id = id;}

    public static User defaultUser() {
        User u = new User();
        u.setId("0");
        return u;
    }

    public User(String firstName, String lastName, String password, String login, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.login = login;
        this.role = role;
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

    @JsonIgnore
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Role getRole() {

        return role == null ? Role.PLAYER : role;
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

    public String getName() {
        return firstName + " " + lastName;
    }

    public Status getStatus() {
        return status;
    }

    public void setHandicapSeasons(Set<HandicapSeason> handicapSeasons) {
        this.handicapSeasons = handicapSeasons;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<HandicapSeason> getHandicapSeasons() {
        return handicapSeasons;
    }

    public void addHandicap(HandicapSeason hc) {
        this.handicapSeasons.add(hc);
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public boolean isChallenge() {
        return handicapSeasons.stream().filter(s->s.getSeason().getDivision().isChallenge()).count() > 0;
    }

    @Override
    public String toString() {
        return "User{   212-9=826 3211 212 570 4821 - c1-1-114-8310756" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", login='" + login + '\'' +
                ", role=" + role +
                '}';
    }
}
