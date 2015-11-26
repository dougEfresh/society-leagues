package com.society.leagues.client.api.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashSet;
import java.util.Set;

public class TeamMembers extends LeagueObject {

    @DBRef User captain;
    @DBRef Set<User> members = new HashSet<>();

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
