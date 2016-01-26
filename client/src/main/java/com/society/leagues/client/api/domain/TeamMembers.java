package com.society.leagues.client.api.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TeamMembers extends LeagueObject {

    @DBRef User captain;
    @DBRef Set<User> members = new HashSet<>();

    public TeamMembers() {
    }

    public TeamMembers(List<User> members) {
        this.members = members == null ? new HashSet<>() : new HashSet<>(members);
    }

    public User getCaptain() {
        return captain;
    }

    public void setCaptain(User captain) {
        this.captain = captain;
    }

    public Set<User> getMembers() {
        return members;
    }
    
    public void addMember(User user) {
        if (user == null)
            return;
        this.members.add(user);
    }

    public boolean removeMember(User user) {
        return this.members.remove(user);
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }
}
