package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.client.View;
import com.society.leagues.client.api.domain.division.Division;

import javax.validation.constraints.NotNull;
import java.util.*;

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
    Date start;
    Date end;

    public Player(Season season, User user, Team team, Handicap handicap, Division division) {
        this.season = season;
        this.user = user;
        this.team = team;
        this.handicap = handicap;
        this.division = division;
    }

    public Player(Season season, User user, Team team, Handicap handicap, Division division, Date start) {
        this.season = season;
        this.user = user;
        this.team = team;
        this.handicap = handicap;
        this.division = division;
        this.start = start;
    }

    public Player() {
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
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

    public Status status() {
        if (end == null)
            return Status.ACTIVE;
        
        if (start == null)
            return Status.PENDING;
        
        return Status.ACTIVE;
        
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Integer getUserId() {
        if (user == null)
            return null;

        return user.getId();
    }

    public void setUserId(Integer userId) {
        this.user = new User(id);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonView(View.PlayerId.class)
    @Override
    public Integer getId() {
        return super.getId();
    }

    @Override
    public String toString() {
        return "Player{" +
                "season=" + season +
                ", division=" + division +
                ", user=" + user +
                ", team=" + team +
                ", handicap=" + handicap +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
