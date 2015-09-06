package com.society.leagues.resource;


import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/playerresult")
@SuppressWarnings("unused")
public class PlayerResultResource {
    @Autowired LeagueService leagueService;


    @RequestMapping(value = "/admin/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PlayerResult create(@RequestBody PlayerResult playerResult) {
        return leagueService.save(playerResult);
    }

    @RequestMapping(value = "/admin/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PlayerResult modify(@RequestBody PlayerResult playerResult) {
        return leagueService.save(playerResult);
    }

    @RequestMapping(value = "/admin/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Boolean delete(@RequestBody PlayerResult playerResult) {
        return leagueService.delete(playerResult);
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public PlayerResult get(Principal principal, @PathVariable String id) {
        return leagueService.findOne(new PlayerResult(id));

    }

    @RequestMapping(value = "/get/season/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<PlayerResult> getPlayerResultSeason(Principal principal, @PathVariable String id) {
        Season s = leagueService.findOne(new Season(id));
        return leagueService.findPlayerResultBySeason(s);
    }

    @RequestMapping(value = "/get/team/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<PlayerResult> getPlayerResulTeam(Principal principal, @PathVariable String id) {
        Team t  = leagueService.findOne(new Team(id));
        return leagueService.findPlayerResultBySeason(t.getSeason()).stream().filter(pr -> pr.hasTeam(t)).collect(Collectors.toList());
    }

    @RequestMapping(value = "/get/user/{id}/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<PlayerResult> getPlayerResultByUser(Principal principal, @PathVariable String id, @PathVariable String type) {
        User u = leagueService.findOne(new User(id));
        if (type.equals("all"))
            return leagueService.findPlayerResultByUser(u)
                    .stream().filter(r->r.getTeamMatch() != null)
                    .sorted((playerResult, t1) -> t1.getMatchDate().compareTo(playerResult.getMatchDate()))
                    .collect(Collectors.toList());

        if (type.equals("current")) {
            return leagueService.findPlayerResultByUser(u)
                    .stream()
                          .filter(r->r.getTeamMatch() != null)
                          .filter(r->r.getSeason().getSeasonStatus() == Status.ACTIVE)
                    .sorted((playerResult, t1) -> t1.getMatchDate().compareTo(playerResult.getMatchDate()))
                    .collect(Collectors.toList());
        }

        return leagueService.findPlayerResultByUser(u)
                .stream()
                .filter(r->r.getTeamMatch() != null)
                .filter(r->r.getSeason().getSeasonStatus() != Status.ACTIVE)
                .sorted((playerResult, t1) -> t1.getMatchDate().compareTo(playerResult.getMatchDate()))
                .collect(Collectors.toList());
    }
}
