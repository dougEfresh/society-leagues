package com.society.leagues.resource;


import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.views.PlayerResultView;
import com.society.leagues.service.LeagueService;
import com.society.leagues.client.views.TeamSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

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
    public Team modify(@RequestBody Team body) {
        Team existingTeam = leagueService.findOne(new Team(body.getId()));
        if (existingTeam == null) {
            existingTeam = new Team();
        }
        existingTeam.setSeason(leagueService.findOne(new Season(body.getSeason().getId())));
        existingTeam.setName(body.getName());
        return leagueService.save(existingTeam);
    }

    @RequestMapping(value = "/admin/modify/members/{teamId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Team modifyMembers(@PathVariable String teamId, @RequestBody TeamMembers teamMembers) {
        TeamMembers existingMembers = leagueService.findOne(teamMembers);
        Team existingTeam = leagueService.findOne(new Team(teamId));
        if (existingTeam == null)
            throw new RuntimeException("Team doesn't exist");

        if (existingMembers == null) {
            existingMembers = new TeamMembers();
        }
        existingMembers.setMembers(new HashSet<>());

        for (User user : teamMembers.getMembers()) {
            existingMembers.addMember(leagueService.findOne(
                    new User(user.getId())
            ));
        }
        leagueService.save(existingMembers);
        existingTeam.setMembers(existingMembers);
        return leagueService.save(existingTeam);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Team get(Principal principal, @PathVariable String id) {
        Team existingTeam = leagueService.findOne(new Team(id));
        if (existingTeam == null) {
            return null;
        }
        return existingTeam;
    }

    @RequestMapping(value = "/user/{id}/{seasonId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Team getTeamForUserSeason(Principal principal, @PathVariable String id, @PathVariable String seasonId) {
        User u = leagueService.findOne(new User(id));
        Season s = leagueService.findOne(new Season(seasonId));
        return leagueService.findAll(Team.class).parallelStream().filter(t->t.getSeason().equals(s) && t.hasUser(u)).findFirst().orElse(null);
    }

    @RequestMapping(value = "/get/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<Team> getTeamSeason(Principal principal, @PathVariable String id) {
         User u = leagueService.findOne(new User(id));
        if (u == null)
             return  Collections.emptyList();

         return leagueService.findAll(Team.class)
                 .stream().parallel()
                 .filter(t->t.hasUser(u) || t.inSameSeason(u))
                 .collect(Collectors.toList()
                 );
    }

    @RequestMapping(value = "/get/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<Team> getUsersTeams(Principal principal) {
         User u = leagueService.findByLogin(principal.getName());
        if (u == null) {
            return Collections.emptyList();
        }
         return leagueService.findAll(Team.class)
                 .stream().parallel()
                 .filter(t->t.hasUser(u))
                 .filter(t->t.getSeason().isActive())
                 .collect(Collectors.toList()
                 );
    }

    @RequestMapping(value = "/user/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<Team> getUsersTeams(@PathVariable String userId, Principal principal) {
         User u = leagueService.findOne(new User(userId));
        if (u == null) {
            return Collections.emptyList();
        }
         return leagueService.findAll(Team.class)
                 .stream().parallel()
                 .filter(t->t.hasUser(u))
                 .filter(t->t.getSeason().isActive())
                 .collect(Collectors.toList()
                 );
    }

    @RequestMapping(value = "/season/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<Team> getTeamBySeason(Principal principal, @PathVariable String id) {
         Season s = leagueService.findOne(new Season(id));
         return leagueService.findAll(Team.class)
                 .stream().parallel()
                 .filter(t->t.getSeason().equals(s))
                 .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                 .collect(Collectors.toList()
                 );
    }


    @RequestMapping(value = "/active", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<Team> getTeamSeason(Principal principal) {
         return leagueService.findCurrent(Team.class);
    }

    @RequestMapping(value = "/get/{id}/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<Team> getTeamSeason(Principal principal, @PathVariable String id, @PathVariable String type) {
        User u = leagueService.findOne(new User(id));
        if (u == null) {
            return  Collections.emptyList();
        }

        return leagueService.findCurrent(Team.class).stream().
                filter(t -> t.getMembers().getMembers().contains(u)).collect(Collectors.toList()
        );
    }

    @RequestMapping(value = "/{id}/members", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public TeamMembers getMembers(Principal principal, @PathVariable String id) {
        Team t =  leagueService.findOne(new Team(id));
        if (t  == null)
            return new TeamMembers();

        return t.getMembers();
    }

}
