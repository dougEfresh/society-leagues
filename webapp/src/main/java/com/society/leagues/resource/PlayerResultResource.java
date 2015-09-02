package com.society.leagues.resource;


import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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

      @RequestMapping(value = "/get/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<PlayerResult> getPlayerResultByUser(Principal principal, @PathVariable String id) {
        User u = leagueService.findOne(new User(id));
        return leagueService.findPlayerResultByUser(u);
    }
}
