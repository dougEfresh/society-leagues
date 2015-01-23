package com.society.leagues.client.api.domain;

import com.society.leagues.client.api.domain.division.Division;

import javax.annotation.security.DenyAll;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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

    Set<Division> divisions;
    Set<Season> seasons;
    Set<Team> teams;
    Set<Player> players;

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

    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> Teams) {
        this.teams = Teams;
    }

    public void addDivisions(List<Division> divisions) {
        if (this.divisions == null) {
            this.divisions = new TreeSet<>();
        }
        this.divisions.addAll(divisions);
    }

    public void addTeams(List<Team> teams) {
        if (this.teams == null) {
            this.teams = new TreeSet<>();
        }
        this.teams.addAll(teams);
    }

    public void addSeasons(List<Season> seasons) {
        if (this.seasons == null) {
            this.seasons = new TreeSet<>();
        }
        this.seasons.addAll(seasons);
    }

    public void addPlayers(List<Player> players) {
        if (this.players == null) {
            this.players = new TreeSet<>();
        }
        this.players.addAll(players);
    }


    public Set<Division> getDivisions() {
        return divisions;
    }

    public void setDivisions(Set<Division> Divisions) {
        this.divisions = Divisions;
    }

    public Set<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(Set<Season> Seasons) {
        this.seasons = Seasons;
    }

    public Set<Player> getPlayers() {
        return players;
    }

    public void setPlayers(Set<Player> Players) {
        this.players = Players;
    }
}
