package com.society.leagues.resource.client;

import com.society.leagues.adapters.TeamMatchAdapter;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.dao.TeamMatchDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
@SuppressWarnings("unused")
public class MatchResource {

    @Autowired TeamMatchDao teamMatchDao;

    @RequestMapping(value = "/match/teams/{seasonId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TeamMatchAdapter> getTeamMatches(@PathVariable Integer seasonId) {
        List<TeamMatch> matches =  teamMatchDao.get().stream().filter(tm->tm.getSeason().getId().equals(seasonId)).collect(Collectors.toList());
        List<TeamMatchAdapter> adapter = new ArrayList<>(matches.size());
        for (TeamMatch match : matches) {
            adapter.add(new TeamMatchAdapter(match));
        }

        adapter.sort(new Comparator<TeamMatchAdapter>() {
            @Override
            public int compare(TeamMatchAdapter o1, TeamMatchAdapter o2) {
                 return o1.getMatchDate().compareTo(o2.getMatchDate());

            }
        });

        return adapter;
    }


    @RequestMapping(value = "/match/teams", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,List<TeamMatchAdapter>> getTeamMatches() {
        Collection<TeamMatch> matches =  teamMatchDao.get();
        Map<Integer,List<TeamMatchAdapter>> adapter = new HashMap<>();
        for (TeamMatch match : matches) {
            if (!adapter.containsKey(match.getSeason().getId())) {
                adapter.put(match.getSeason().getId(), new ArrayList<>());
            }
            adapter.get(match.getSeason().getId()).add(new TeamMatchAdapter(match));
        }
        for (Integer integer : adapter.keySet()) {
            adapter.get(integer).sort(new Comparator<TeamMatchAdapter>() {
                @Override
                public int compare(TeamMatchAdapter o1, TeamMatchAdapter o2) {
                    return o1.getMatchDate().compareTo(o2.getMatchDate());
                }
            });
        }
        return adapter;
    }
}
