package com.society.leagues.resource;


import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(value = "/api/team")
@SuppressWarnings("unused")
public class TeamResource {

    @Autowired LeagueService leagueService;

    @RequestMapping(value = "/admin/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Team create(@RequestBody Team team) {
        return leagueService.save(team);
    }

    @RequestMapping(value = "/admin/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Team modify(@RequestBody Team team) {
        Team existingTeam = leagueService.findOne(team);
        if (existingTeam == null) {
            return null;
        }
        return leagueService.save(team);
    }

    @RequestMapping(value = "/admin/members/add/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Team addMembers(Principal principal, @PathVariable String id, @RequestBody List<User> users) {
        Team existingTeam = leagueService.findOne(new Team(id));

        if (existingTeam == null) {
            return null;
        }
        existingTeam.addMembers(users);
        return leagueService.save(existingTeam);
    }

    @RequestMapping(value = "/admin/members/remove/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Team removeMembers(Principal principal, @PathVariable String id, @RequestBody List<User> users) {
        Team existingTeam = leagueService.findOne(new Team(id));
        if (existingTeam == null) {
            return null;
        }
        existingTeam.removeMembers(users);
        return leagueService.save(existingTeam);
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Team get(Principal principal, @PathVariable String id) {
        Team existingTeam = leagueService.findOne(new Team(id));
        if (existingTeam == null) {
            return null;
        }
        return existingTeam;
    }

    @RequestMapping(value = "/get/season/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<Team> getTeamSeason(Principal principal, @PathVariable String id) {
         Season s = leagueService.findOne(new Season(id));
         return leagueService.findTeamBySeason(s);
    }
}
