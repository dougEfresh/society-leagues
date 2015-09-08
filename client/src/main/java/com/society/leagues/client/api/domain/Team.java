package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.client.api.domain.converters.DateTimeDeSerializer;
import com.society.leagues.client.api.domain.converters.DateTimeSerializer;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Set<User> getMembers() {
        HashSet users = new HashSet();
        users.addAll(members.stream()
                .filter(u->!u.isFake())
                .collect(Collectors.toList()));
        return users;
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
      return season.isNine();
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
