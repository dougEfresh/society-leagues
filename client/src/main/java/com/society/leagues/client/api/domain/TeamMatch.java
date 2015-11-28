package com.society.leagues.client.api.domain;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.converters.DateTimeDeSerializer;
import com.society.leagues.converters.DateTimeSerializer;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@SuppressWarnings("unused")
public class TeamMatch extends LeagueObject {

    @NotNull @DBRef Team home;
    @NotNull @DBRef Team away;
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    @NotNull LocalDateTime matchDate;

    Division division = null;
    Integer homeRacks = 0;
    Integer awayRacks = 0;
    Integer setHomeWins = 0;
    Integer setAwayWins = 0;
    Integer matchNumber = 0;
    User referenceUser = null;
    Boolean hasPlayerResults = false;
    Status status;
    Integer forfeits = 0;
    Integer homeForfeits = 0;
    Integer awayForfeits = 0;
    Integer handicapRacks = 0;

    public TeamMatch(Team home, Team away, LocalDateTime matchDate) {
        this.home = home;
        this.away = away;
        this.matchDate = matchDate;
    }

    public TeamMatch() {
    }

    public Status getStatus() {
        LocalDateTime now  = LocalDateTime.now();
        if (getMatchDate().isBefore(now) && !isHasResults()) {
            return Status.PENDING;
        }
        return status;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TeamMatch(String id) {
        this.id = id;
    }

    public Team getHome() {
        return home;
    }

    public void setHome(Team home) {
        this.home = home;
    }

    public Team getAway() {
        return away;
    }

    public void setAway(Team away) {
        this.away = away;
    }

    public LocalDateTime getMatchDate() {
        return matchDate;
    }

    public void setMatchDate(LocalDateTime matchDate) {
        this.matchDate = matchDate;
    }

    public Season getSeason() {
        return home.getSeason();
    }

    public String score() {
        return String.format("%s-%s", getHomeRacks(), getAwayRacks());
    }

    public Division getDivision() {

        if (division == null && getSeason() != null) {
            return getSeason().getDivision();
        }
        return division;
    }

    public boolean isNine() {
        return getSeason().isNine();
    }

    public boolean isChallenge() {
        return getDivision().isChallenge();
    }

    public Integer getAwayRacks() {
        return awayRacks;
    }

    public void setAwayRacks(Integer awayRacks) {
        this.awayRacks = awayRacks;
    }

    public Integer getHomeRacks() {
        return homeRacks;
    }

    public void setHomeRacks(Integer homeRacks) {
        this.homeRacks = homeRacks;
    }

    public boolean hasUser(User user) {
        return home.getMembers().getMembers().contains(user) || away.getMembers().getMembers().contains(user);
    }

    public boolean hasTeam(Team team) {
        if (home == null || away == null) {
            return false;
        }
        return  home.equals(team) || away.equals(team);
    }

    public boolean isWinner(Team t) {
        if (t.equals(home) ) {
            return homeRacks > awayRacks;
        }
        return awayRacks > homeRacks;
    }

    public Integer getRacks(Team t) {
        if (t.equals(home) ) {
            return homeRacks;
        }
        return awayRacks;
    }

    public Integer getOpponentRacks(Team t) {
        if (t.equals(home) ) {
            return awayRacks;
        }
        return homeRacks;
    }

    public Integer getSetAwayWins() {
        return setAwayWins;
    }

    public void setSetAwayWins(Integer setAwayWins) {
        this.setAwayWins = setAwayWins;
    }

    public Integer getSetHomeWins() {
        return setHomeWins;
    }

    public void setSetHomeWins(Integer setHomeWins) {
        this.setHomeWins = setHomeWins;
    }

    public boolean isHasResults() {
        return homeRacks + awayRacks > 0;
    }

    public Integer getSetWins(Team team) {
        if (!team.isNine()) { return isWinner(team) ? 1 : 0; }

        return team.equals(home)? setHomeWins : setAwayWins;
    }

    public Integer getSetLoses(Team team) {
         if (!team.isNine()) { return isWinner(team) ? 1 : 0; }

        return team.equals(home)? setAwayWins : setHomeWins;
    }

    public Integer getWinnerSetWins() {
        if (!getSeason().isNine() || getSeason().getDivision().isChallenge()) {
            return null;
        }

        return homeRacks > awayRacks ? setHomeWins : setAwayWins;
    }

    public Integer getWinnerSetLoses() {
        if (!getSeason().isNine() || getSeason().getDivision().isChallenge()) {
            return null;
        }

        return homeRacks > awayRacks ? setAwayWins : setHomeWins;
    }

    public Integer getLoserSetWins() {
        if (!getSeason().isNine() || getSeason().getDivision().isChallenge()) {
            return  homeRacks > awayRacks  ? awayRacks  : homeRacks;
        }

        return homeRacks > awayRacks ? setAwayWins : setHomeWins;
    }

    public Integer getLoserSetLoses() {
        if (!getSeason().isNine() || getSeason().getDivision().isChallenge()) {
            return  homeRacks > awayRacks  ? homeRacks  : awayRacks;
        }

        return homeRacks > awayRacks ? setHomeWins : setAwayWins;
    }

    public Integer getSetHomeLost() {
        return setAwayWins;
    }

    public Integer getSetAwayLost() {
        return setHomeWins;
    }

    public Team getOpponentTeam() {
        if (referenceUser == null)
            return null;

        return home.getMembers().getMembers().contains(referenceUser) ? away : home;
    }

    public Integer getWinnerRacks() {
        if (!isHasResults())
            return null;

        return homeRacks > awayRacks ? homeRacks : awayRacks;
    }

    public Integer getLoserRacks() {
        if (!isHasResults())
            return null;

        return homeRacks > awayRacks ? awayRacks : homeRacks;
    }

    public Team getWinner() {
        if (homeRacks.equals(awayRacks))
            return home;

        return  homeRacks > awayRacks ? home : away;
    }

    public Team getLoser() {
        if (awayRacks.equals(homeRacks)) {
            return away;
        }
        return awayRacks > homeRacks ? home : away;
    }

    public String getRace() {
        if (getSeason().isChallenge()) {
            Handicap h  = getHome().getChallengeUser().getHandicap(getSeason());
            Handicap a = getAway().getMembers().getMembers().iterator().next().getHandicap(getSeason());
            return Handicap.race(h,a);
        } else {
            return "";
        }
    }

    public User getChallenger() {
        if (getSeason().isChallenge()) {
            return getHome().getChallengeUser();
        }
        return null;
    }


    public User getOpponent() {
        if (getSeason().isChallenge()) {
            return getAway().getChallengeUser();
        }
        return null;
    }

    public boolean isForfeit() {
        return (homeRacks == 0 && awayRacks > 0) || (awayRacks == 0 && homeRacks > 0);
    }

    public Integer getForfeits() {
        return forfeits;
    }

    public void setForfeits(Integer forfeits) {
        this.forfeits = forfeits;
    }

    public Integer getHandicapRacks() {
        return handicapRacks;
    }

    public void setHandicapRacks(Integer handicapRacks) {
        this.handicapRacks = handicapRacks;
    }

    public Integer getHomeForfeits() {
        return homeForfeits;
    }

    public void setHomeForfeits(Integer homeForfeits) {
        this.homeForfeits = homeForfeits;
    }

    public Integer getAwayForfeits() {
        return awayForfeits;
    }

    public void setAwayForfeits(Integer awayForfeits) {
        this.awayForfeits = awayForfeits;
    }



    @Override
    public String toString() {
        return "TeamMatch{" +
                "home=" + home +
                ", away=" + away +
                ", matchDate=" + matchDate +
                ", homeRacks=" + homeRacks +
                ", awayRacks=" + awayRacks +
                ", setHomeWins=" + setHomeWins +
                ", setAwayWins=" + setAwayWins +
                '}';
    }


    public Boolean getHasPlayerResults() {
        return hasPlayerResults;
    }

    public void setHasPlayerResults(Boolean hasPlayerResults) {
        this.hasPlayerResults = hasPlayerResults;
    }

    public Integer getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }

    public void setReferenceUser(User referenceUser) {
        this.referenceUser = referenceUser;
    }


}
