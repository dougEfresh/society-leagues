package com.society.leagues.model;

import com.society.leagues.client.api.domain.*;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public class TeamModel extends Team {

    List<User> users = new ArrayList<>();
    String membersId;

    public String getMembersId() {
        return membersId;
    }

    public void setMembersId(String membersId) {
        this.membersId = membersId;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public boolean hasUser(String id) {
        User u = new User(id);
        return users.stream().filter(user->user.equals(u)).count() > 0;
    }

    public static TeamModel fromTeam(Team team) {
        TeamModel tm = new TeamModel();
        if (team == null || team.getId() == null) {
            Season s = new Season();
            tm.setSeason(Season.getDefault());
            tm.setId("new");
            return tm;
        }

        ReflectionUtils.shallowCopyFieldState(team,tm);
        return tm;
    }
}
