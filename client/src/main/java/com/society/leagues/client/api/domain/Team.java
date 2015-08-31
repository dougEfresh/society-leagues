package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.client.api.domain.converters.DateTimeDeSerializer;
import com.society.leagues.client.api.domain.converters.DateTimeSerializer;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Team extends LeagueObject {

    @DBRef Season season;
    @NotNull String name;
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime created;
    @DBRef List<User> members;
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

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public void addMember(User user) {
        if (this.members == null) {
            this.members = new ArrayList<>();
        }
        this.members.add(user);
    }

    public void addMembers(List<User> users) {
        if (this.members == null) {
            this.members = new ArrayList<>();
        }
        for (User user : users) {
            if (!this.members.contains(user)) {
                this.members.add(user);
            }
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
}
