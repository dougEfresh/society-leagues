package com.society.leagues.resource;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.service.LeagueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/season")
@SuppressWarnings("unused")
public class SeasonResource {

    @Autowired LeagueService leagueService;
    static Logger logger = LoggerFactory.getLogger(SeasonResource.class);

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Season get(Principal principal, @PathVariable String id) {
        return leagueService.findOne(new Season(id));
    }

    @RequestMapping(value = {"/active","/current"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<Season> getActiveSeasons(Principal principal) {
        return leagueService.findAll(Season.class).stream().filter(s->s.isActive()).collect(Collectors.toList());
    }

    @RequestMapping(value = "/admin/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Season create(Principal principal, @RequestBody Season season) {
        Season previous = leagueService.findAll(Season.class).stream().filter(s->s.getDivision() == season.getDivision()).max(new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o2.getStartDate().compareTo(o2.getStartDate());
            }
        }).get();
        Season newSeason = leagueService.save(season);
        logger.info("Saved new season " + season.getId() + " " + season.getDisplayName());
        List<Team> teams = leagueService.findAll(Team.class).parallelStream().filter(t->t.getSeason().equals(previous)).collect(Collectors.toList());
        List<Team> newTeams =  new ArrayList<>();
        for (Team team : teams) {
            Team newTeam = LeagueObject.copy(team);
            newTeam.setId(null);
            newTeam.setSeason(newSeason);
            for( User user : newTeam.getMembers().getMembers()) {
                Handicap handicap = user.getHandicap(previous);
                user.addHandicap(new HandicapSeason(handicap,newSeason));
                leagueService.save(user);
            }
            logger.info("Adding team " + team.getName());
            newTeams.add(newTeam);
        }
        leagueService.save(newTeams);
        return season;
    }

    @RequestMapping(value = "/admin/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Season modify(Principal principal, @RequestBody Season season) {
        return leagueService.save(season);
    }

    @RequestMapping(value = {"/get","/",""}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<Season> getSeasons(Principal principal) {
        return leagueService.findAll(Season.class).stream().filter(s->s.getDivision() != Division.STRAIGHT)
                .sorted(new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o2.getStartDate().compareTo(o1.getStartDate());
            }
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = {"/divisions"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<Division> getDivisions(Principal principal) {
        List<Division> divisions = Arrays.asList(Division.values());
        divisions.sort(new Comparator<Division>() {
            @Override
            public int compare(Division division, Division t1) {
                return division.name().compareTo(t1.name());
            }
        });
        return divisions;
    }

    @RequestMapping(value = {"/handicaps"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Handicap[] getHandicaps(Principal principal) {
        return Handicap.values();
    }
}
