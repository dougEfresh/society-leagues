package com.society.leagues.client.api.domain;

import com.society.leagues.client.api.domain.division.Division;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.List;

public class Player extends LeagueObject {
    @NotNull
    Season season;
    @NotNull
    Division division;
    @NotNull
    User user;
    @NotNull
    Team team;
    @NotNull
    Handicap handicap;
    List<Match> matches;
    @NotNull
    Status status = Status.ACTIVE;
    
    public Player(Season season, User user, Team team, Handicap handicap, Division division) {
        this.season = season;
        this.user = user;
        this.team = team;
        this.handicap = handicap;
        this.division = division;
    }

    public Player() {
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public Handicap getHandicap() {
        return handicap;
    }

    public void setHandicap(Handicap handicap) {
        this.handicap = handicap;
    }

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }
}
