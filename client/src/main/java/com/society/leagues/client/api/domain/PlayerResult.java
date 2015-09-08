package com.society.leagues.client.api.domain;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.society.leagues.client.api.domain.converters.DateTimeDeSerializer;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

public class PlayerResult extends LeagueObject {

    @DBRef TeamMatch teamMatch;
    @DBRef User playerHome;
    @DBRef User playerAway;
    @DBRef Season season; //TODO remove reference
    Integer homeRacks;
    Integer awayRacks;
    Integer matchNumber;
    Handicap playerHomeHandicap;
    Handicap playerAwayHandicap;

    public PlayerResult() {
    }

    public PlayerResult(TeamMatch teamMatch, User playerHome, User playerAway, Season season, Integer homeRacks, Integer awayRacks, Integer matchNumber, Handicap playerHomeHandicap, Handicap playerAwayHandicap) {
        this.teamMatch = teamMatch;
        this.playerHome = playerHome;
        this.playerAway = playerAway;
        this.season = season;
        this.homeRacks = homeRacks;
        this.awayRacks = awayRacks;
        this.matchNumber = matchNumber;
        this.playerHomeHandicap = playerHomeHandicap;
        this.playerAwayHandicap = playerAwayHandicap;
    }

    public PlayerResult(String id) {
        this.id = id;
    }

    public TeamMatch getTeamMatch() {
        return teamMatch;
    }

    public void setTeamMatch(TeamMatch teamMatch) {
        this.teamMatch = teamMatch;
    }

    public User getPlayerHome() {
        return playerHome;
    }

    public void setPlayerHome(User playerHome) {
        this.playerHome = playerHome;
    }

    public User getPlayerAway() {
        return playerAway;
    }

    public void setPlayerAway(User playerAway) {
        this.playerAway = playerAway;
    }

    public Integer getHomeRacks() {
        return homeRacks;
    }

    public void setHomeRacks(Integer homeRacks) {
        this.homeRacks = homeRacks;
    }

    public Integer getAwayRacks() {
        return awayRacks;
    }

    public void setAwayRacks(Integer awayRacks) {
        this.awayRacks = awayRacks;
    }

    public Integer getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Handicap getPlayerHomeHandicap() {
        return playerHomeHandicap;
    }

    public void setPlayerHomeHandicap(Handicap playerHomeHandicap) {
        this.playerHomeHandicap = playerHomeHandicap;
    }

    public Handicap getPlayerAwayHandicap() {
        return playerAwayHandicap;
    }

    public void setPlayerAwayHandicap(Handicap playerAwayHandicap) {
        this.playerAwayHandicap = playerAwayHandicap;
    }

    public User getWinner() {
        if (homeRacks  == null ||  awayRacks == null)
            return null;

        return homeRacks > awayRacks ? playerHome : playerAway;
    }

    public Integer getWinnerRacks() {
        if (homeRacks  == null ||  awayRacks == null)
            return 0;

        return homeRacks > awayRacks ? homeRacks : awayRacks;
    }

    public Handicap getWinnerHandicap() {
        if (getWinner() == null)
            return null;
        HandicapSeason hc =  getWinner().getHandicapSeasons().stream().filter(s->s.getSeason().equals(getSeason())).findFirst().orElse(null);
        if (hc == null) {return null;}
        return hc.getHandicap();
    }

    public User getLoser() {
         if (homeRacks  == null ||  awayRacks == null)
            return null;

        return homeRacks > awayRacks ? playerAway : playerHome;
    }

    public Integer getLoserRacks() {
        if (homeRacks  == null ||  awayRacks == null)
            return 0;

        return homeRacks > awayRacks ? awayRacks : homeRacks;
    }

    public Handicap getLoserHandicap() {
        if (getLoser() == null)
            return null;
        HandicapSeason hc =  getLoser().getHandicapSeasons().stream().filter(s->s.getSeason().equals(getSeason())).findFirst().orElse(null);
        if (hc == null) {return null;}
        return hc.getHandicap();
    }

    public boolean isWinner(User u) {
        return u != null && u.equals(getWinner());
    }

    @JsonDeserialize(using = DateTimeDeSerializer.class)
    public LocalDateTime getMatchDate() {
        if (getTeamMatch() == null)
            return null;
        return getTeamMatch().getMatchDate();
    }

    public boolean hasUser(User u) {
        return u!= null && (u.equals(playerHome) || u.equals(playerAway));
    }

    public boolean hasTeam(Team t) {
        return t!= null && (t.equals(getTeamMatch().getHome()) || t.equals(getTeamMatch().getAway()));
    }

    public Handicap getHandicap(User u) {
        if (u.equals(playerAway)) {
            return playerAwayHandicap ;
        }
        if (u.equals(playerHome)) {
            return playerAwayHandicap ;
        }
        return null;
    }

    public boolean isNine() {
        return getSeason().getDivision() == Division.NINE_BALL_TUESDAYS || getSeason().getDivision() == Division.NINE_BALL_CHALLENGE;
    }

    @Override
    public String toString() {
        return "PlayerResult{" +
                "teamMatch=" + teamMatch +
                ", playerHome=" + playerHome +
                ", playerAway=" + playerAway +
                ", season=" + season +
                ", homeRacks=" + homeRacks +
                ", awayRacks=" + awayRacks +
                ", matchNumber=" + matchNumber +
                ", playerHomeHandicap=" + playerHomeHandicap +
                ", playerAwayHandicap=" + playerAwayHandicap +
                '}';
    }
}
