package com.society.leagues.client.api.domain;

import com.society.leagues.client.api.domain.division.Division;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

public class Player extends LeagueObject {
    @NotNull
    Season season;
    @NotNull
    Division division;
    @NotNull
    Integer userId;
    @NotNull
    Team team;
    @NotNull
    Handicap handicap;
    List<Match> matches;
    List<TeamResult> teamResults;
    List<Challenge> challenges;
    Date start;
    Date end;
    
    public Player(Season season, User user, Team team, Handicap handicap, Division division) {
        this.season = season;
        this.userId = user.getId();
        this.team = team;
        this.handicap = handicap;
        this.division = division;
    }

    public Player(Season season, User user, Team team, Handicap handicap, Division division, Date start) {
        this.season = season;
        this.userId = user.getId();
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

    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
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
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<TeamResult> getTeamResults() {
        return teamResults;
    }

    public void setTeamResults(List<TeamResult> teamResults) {
        this.teamResults = teamResults;
    }

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    @Override
    public String toString() {
        return "Player{" +
                "season=" + season +
                ", division=" + division +
                ", user=" + userId +
                ", team=" + team +
                ", handicap=" + handicap +
                ", matches=" + matches +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}
