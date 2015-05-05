package com.society.leagues.resource.client;

import com.society.leagues.adapters.TeamAdapter;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.dao.PlayerDao;
import com.society.leagues.dao.TeamDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RequestMapping(value = "/api")
@RestController
public class TeamResource {

    @Autowired TeamDao teamDao;
    @Autowired PlayerDao playerDao;

    @RequestMapping(value = "/teams", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TeamAdapter> teams() {
        List<TeamAdapter> teams = new ArrayList<>();
        Collection<Player> players = playerDao.get();

        for (Team team: teamDao.get()) {
            List<Player> teamPlayer = players.stream().filter(p->p.getTeam().equals(team)).collect(Collectors.toList());
            List<Season> seasonPlayers = teamPlayer.stream().map(Player::getSeason).collect(Collectors.toList());
            teams.add(new TeamAdapter(team,seasonPlayers));
        }
        return teams;
    }
}
