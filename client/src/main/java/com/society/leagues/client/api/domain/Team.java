package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Team extends LeagueObject {

    @NotNull @DBRef Season season;
    @NotNull String name;
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime created;
    @DBRef Set<User> members = new HashSet<>();
    @DBRef User captain;

    public Team(Season season, String name) {
        this.season = season;
        this.name = name;
        this.created = LocalDateTime.now();
    }

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

    //@JsonView(value = {TeamSummary.class, PlayerResultView.class})
    @JsonIgnore
    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public void addMember(User user) {
        this.members.add(user);
    }

    public void addMembers(List<User> users) {
        for (User user : users) {
            this.members.add(user);
        }
    }

   public void removeMember(User user) {
        if (this.members == null) {
            return;
        }
        this.members.remove(user);
    }

    public void removeMembers(List<User> users) {
        if (this.members == null) {
            return;
        }
        for (User user : users) {
            this.members.remove(user);
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
        return captain;
    }

    public void setCaptain(User captain) {
        this.captain = captain;
    }

    public boolean isNine() {
      return season != null && season.isNine();

    }

    public boolean hasUser(User u) {
        if (u == null)
            return false;

        return members.contains(u);
    }

    public boolean inSameSeason(User u) {
        for (HandicapSeason handicapSeason : u.getHandicapSeasons()) {
            if (handicapSeason.getSeason().equals(season)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Team{" +
                "season=" + season +
                ", name='" + name + '\'' +
                ", created=" + created +
                ", captain=" + captain +
                '}';
    }
}
