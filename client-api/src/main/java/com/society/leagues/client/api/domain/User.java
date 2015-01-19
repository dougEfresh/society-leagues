package com.society.leagues.client.api.domain;

import com.society.leagues.client.api.Role;

import javax.annotation.security.DenyAll;
import javax.validation.constraints.NotNull;
import java.util.List;

@SuppressWarnings("unused")
public class User extends LeagueObject {

    @NotNull
    String firstName;
    @NotNull
    String lastName;
    String email;
    String password;
    @NotNull
    String login;
    @NotNull
    Role role;
    
    List<Team> currentTeams;
    List<Team> pastTeams;
    List<Match> currentMatches;
    List<Match> pastMatches;
    List<Player> currentPlayers;
        
    public User(String login, String password, Role role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }
    
    public User (Integer id) {
        setId(id);
    }

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.role = Role.PLAYER;
    }

    public User() {

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

    public List<Team> getCurrentTeams() {
        return currentTeams;
    }

    public void setCurrentTeams(List<Team> currentTeams) {
        this.currentTeams = currentTeams;
    }

    public List<Team> getPastTeams() {
        return pastTeams;
    }

    public void setPastTeams(List<Team> pastTeams) {
        this.pastTeams = pastTeams;
    }

    public List<Match> getCurrentMatches() {
        return currentMatches;
    }

    public void setCurrentMatches(List<Match> currentMatches) {
        this.currentMatches = currentMatches;
    }

    public List<Match> getPastMatches() {
        return pastMatches;
    }

    public void setPastMatches(List<Match> pastMatches) {
        this.pastMatches = pastMatches;
    }

    public List<Player> getCurrentPlayers() {
        return currentPlayers;
    }

    public void setCurrentPlayers(List<Player> currentPlayers) {
        this.currentPlayers = currentPlayers;
    }
}
