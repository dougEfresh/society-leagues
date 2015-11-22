package com.society.leagues.service;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMembers;
import com.society.leagues.client.api.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class TeamService {

    @Autowired LeagueService leagueService;

     public Team createTeam(String name, Season season) {
         return createTeam(name,season,new ArrayList<>());
    }

    public Team createTeam(String name, Season season, List<User> members) {
        Team t = leagueService.findAll(Team.class).parallelStream()
                .filter(team->team.getName().equals(name))
                .filter(team->team.getSeason().equals(season))
                .findFirst().orElse(new Team());
        t.setSeason(season);
        t.setName(name);
        TeamMembers m = t.getMembers() ==  null ? new TeamMembers() : t.getMembers();
        for (User member : members) {
            m.addMember(member);
        }
        m = leagueService.save(m);
        t.setMembers(m);
        return leagueService.save(t);
    }
}
