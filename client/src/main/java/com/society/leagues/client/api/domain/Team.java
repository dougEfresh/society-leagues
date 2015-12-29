package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.converters.DateTimeDeSerializer;
import com.society.leagues.converters.DateTimeSerializer;
import com.society.leagues.client.views.PlayerResultView;
import com.society.leagues.client.views.TeamSummary;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class Team extends LeagueObject {

    @NotNull @DBRef Season season;
    @NotNull String name;
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime created;
    @DBRef TeamMembers members;
    Integer rank = 0;
    public Team(Season season, String name) {
        this.season = season;
        this.name = name;
        this.created = LocalDateTime.now();
    }
    Stat stats = new Stat();

    public Team(String id) {
        this.id = id;
    }

    public Team() {

    }

    @JsonView(value = {TeamSummary.class, PlayerResultView.class})
    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public TeamMembers getMembers() {
        if (members == null)
            return new TeamMembers();

        return members;
    }

    public void addMember(User user) {
        this.members.addMember(user);
    }

    public void addMembers(List<User> users) {
        for (User user : users) {
            this.members.addMember(user);
        }
    }

    public void removeMembers(List<User> users) {
        if (this.members == null) {
            return;
        }
        for (User user : users) {
            this.members.removeMember(user);
        }
    }

    @JsonView(value = {TeamSummary.class,PlayerResultView.class})
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public User getCaptain() {
        return this.members == null ? null : this.members.getCaptain();
    }

    public boolean isNine() {
      return season != null && season.isNine();

    }

    public boolean hasUser(User u) {
        if (u == null)
            return false;
        if (members == null) {
            return false;
        }
        return members.getMembers().contains(u);
    }

    public boolean inSameSeason(User u) {
        for (HandicapSeason handicapSeason : u.getHandicapSeasons()) {
            if (handicapSeason.getSeason().equals(season)) {
                return true;
            }
        }
        return false;
    }

    public boolean isChallenge() {
        return season != null && season.getDivision().isChallenge();
    }

    public User getChallengeUser() {
        if (!isChallenge() || members == null || members.getMembers().isEmpty()) {
            return null;
        }
        return members.getMembers().iterator().next();
    }

    @JsonView(value = PlayerResultView.class)
    public void setMembers(TeamMembers members) {
        this.members = members;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }


    public Stat getStats() {
        return stats;
    }

    public void setStats(Stat stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "Team{" +
                "season=" + season +
                ", name='" + name + '\'' +
                ", created=" + created +
                '}';
    }
}
