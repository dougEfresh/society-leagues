package com.society.admin.model;

import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public class TeamModel extends Team {

    List<User> users = new ArrayList<>();

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
        ReflectionUtils.shallowCopyFieldState(team,tm);
        return tm;
    }
}
