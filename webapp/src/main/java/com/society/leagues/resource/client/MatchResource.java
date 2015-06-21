package com.society.leagues.resource.client;

import com.society.leagues.adapters.TeamMatchAdapter;
import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.dao.PlayerResultDao;
import com.society.leagues.dao.TeamMatchDao;
import com.society.leagues.dao.TeamResultDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
@SuppressWarnings("unused")
public class MatchResource {

    @Autowired TeamMatchDao teamMatchDao;
    @Autowired TeamResultDao teamResultDao;
    @Autowired PlayerResultDao playerResultDao;

    @RequestMapping(value = "/match/teams/{seasonId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<LocalDateTime,List<TeamMatchAdapter>> getTeamMatches(@PathVariable Integer seasonId) {
        List<TeamMatch> matches =  teamMatchDao.get().stream().filter(tm->tm.getSeason().getId().equals(seasonId)).collect(Collectors.toList());
        Map<LocalDateTime,List<TeamMatchAdapter>> retVal = new TreeMap<>();

        matches.forEach(teamMatch -> retVal.put(teamMatch.getMatchDate(),new ArrayList<>()));

        for (LocalDateTime date : retVal.keySet()) {
            List<TeamMatchAdapter> matchDate = new ArrayList<>(10);
            for (TeamMatch m : matches.stream().filter(tm->tm.getMatchDate().isEqual(date)).collect(Collectors.toList())) {
                TeamMatchAdapter adapter = new TeamMatchAdapter(m,
                        teamResultDao.getByMatch(m.getId()),
                        playerResultDao.get().stream()
                                .filter(p->p.getTeamMatch().getId().equals(m.getId()))
                                .collect(Collectors.toList()));
                matchDate.add(adapter);
            }
            retVal.put(date,matchDate);
        }
        return retVal;
    }



    @RequestMapping(value = "/match/teams/current", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TeamMatchAdapter> getTeamMatchesCurrent() {
        List<TeamMatch> matches = teamMatchDao.get().stream().
                filter(m->m.getSeason().getSeasonStatus() == Status.ACTIVE).
                filter(m->m.getSeason().getDivision().isChallenge()).
                collect(Collectors.toList());
        List<TeamMatchAdapter> adapter = new ArrayList<>();
        for (TeamMatch match : matches) {
            adapter.add(new TeamMatchAdapter(
                    match,
                    teamResultDao.getByMatch(match.getId()),
                    playerResultDao.get().stream().filter(p->p.getTeamMatch().getId().equals(match.getId())).collect(Collectors.toList())));
        }
        return adapter;
    }


}
