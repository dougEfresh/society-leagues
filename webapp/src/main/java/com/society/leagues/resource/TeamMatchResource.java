package com.society.leagues.resource;

import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/teammatch")
@SuppressWarnings("unused")
public class TeamMatchResource {

    @Autowired LeagueService leagueService;

    @RequestMapping(value = "/admin/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch create(@RequestBody TeamMatch teamMatch) {
        Team home = leagueService.findOne(teamMatch.getHome());
        Team away = leagueService.findOne(teamMatch.getAway());
        teamMatch.setHome(home);
        teamMatch.setAway(away);
        return leagueService.save(teamMatch);
    }

    @RequestMapping(value = "/admin/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch modify(@RequestBody TeamMatch teamMatch) {
        Team home = leagueService.findOne(teamMatch.getHome());
        Team away = leagueService.findOne(teamMatch.getAway());
        teamMatch.setHome(home);
        teamMatch.setAway(away);
        return leagueService.save(teamMatch);
    }

    @RequestMapping(value = "/admin/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Boolean delete(@RequestBody TeamMatch teamMatch) {
        return leagueService.delete(teamMatch);
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public TeamMatch get(Principal principal, @PathVariable String id) {
        return leagueService.findOne(new TeamMatch(id));

    }

    @RequestMapping(value = "/get/season/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<TeamMatch> getTeamMatchSeason(Principal principal, @PathVariable String id) {
         Season s = leagueService.findOne(new Season(id));
         return leagueService.findTeamMatchBySeason(s);
    }
}
