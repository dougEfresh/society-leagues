package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.society.leagues.client.api.domain.division.Division;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class,
                  property = "@id")
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
    List<TeamMatch> teamMatches;
    List<PlayerResult> playerResults;
    List<Challenge> challenges;
    Date start;
    Date end;
    Integer wins = 0;
    Integer loses = 0;
    Integer racks = 0;

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

    public List<TeamMatch> getTeamMatches() {
        return teamMatches;
    }

    public void setTeamMatches(List<TeamMatch> teamMatches) {
        this.teamMatches = teamMatches;
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
        return user.getId();
    }

    public void setUserId(Integer userId) {
        this.user = new User(id);
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Player{" +
                "season=" + season +
                ", division=" + division +
                ", user=" + user +
                ", team=" + team +
                ", handicap=" + handicap +
                ", matches=" + teamMatches +
                ", start=" + start +
                ", end=" + end +
                '}';
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLoses() {
        return loses;
    }

    public void setLoses(Integer loses) {
        this.loses = loses;
    }

    public Integer getRacks() {
        return racks;
    }

    public void setRacks(Integer racks) {
        this.racks = racks;
    }

    public void addWin() {
        wins++;
    }

    public void addLost() {
        loses++;
    }

    public void addRacks(Integer r) {
        if (r == null)
            return;

        this.racks += r;
    }

}
