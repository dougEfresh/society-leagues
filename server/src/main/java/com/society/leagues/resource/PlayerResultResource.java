package com.society.leagues.resource;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.society.leagues.client.views.PlayerResultSummary;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.ResultService;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.views.PlayerResultView;
import com.society.leagues.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/playerresult")
@SuppressWarnings("unused")
public class PlayerResultResource {
    @Autowired LeagueService leagueService;
    @Autowired ResultService resultService;
    @Autowired StatService statService;
    @Autowired ObjectMapper objectMapper;

    @RequestMapping(value = "/admin/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<PlayerResult> modify(@RequestBody List<PlayerResult> playerResult) {
        return resultService.createOrModify(playerResult);
    }

    @RequestMapping(value = "/admin/delete/{id}", method = {RequestMethod.GET, RequestMethod.DELETE }, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Boolean delete(@PathVariable String id) {
        leagueService.purge(new PlayerResult(id));
        return true;
    }

    @RequestMapping(value = "/teammatch/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<PlayerResult> getPlayerResultTeamMatch(Principal principal, @PathVariable String id) {
        TeamMatch tm = leagueService.findOne(new TeamMatch(id));
        if (tm == null) {
            return Collections.emptyList();
        }
        Collection<PlayerResult> results;
        if (tm.getSeason().isActive()) {
            results = leagueService.findCurrent(PlayerResult.class);
        }  else {
            results = leagueService.findAll(PlayerResult.class);
        }
        if (results.isEmpty()) {
            results = resultService.createNewPlayerResults(tm);
        }
        results = results.stream().parallel().
                filter(pr -> pr.getTeamMatch().equals(tm))
                .sorted(new Comparator<PlayerResult>() {
                    @Override
                    public int compare(PlayerResult playerResult, PlayerResult t1) {
                        return playerResult.getMatchNumber().compareTo(t1.getMatchNumber());
                    }
        }).collect(Collectors.toList());
        User u = leagueService.findByLogin(principal.getName());
        if (results.isEmpty() && u.isAdmin()) {
            return Arrays.asList(resultService.createNewPlayerResults(tm).toArray(new PlayerResult[]{}));
        }
        List<PlayerResult> copy = new ArrayList<>(results.size());
        for (PlayerResult result : results) {
            copy.add(PlayerResult.copy(result));
        }
        copy.stream().parallel().
                forEach(pr->pr.setReferenceTeam(
                        pr.getTeamMatch().getHomeRacks() > pr.getTeamMatch().getAwayRacks() ?
                                pr.getTeamMatch().getHome() : pr.getTeamMatch().getAway())
                );
        return copy;
    }

    @JsonView(PlayerResultSummary.class)
    @RequestMapping(value = "/teammatch/{id}/summary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Collection<PlayerResult> getPlayerResults(Principal principal, @PathVariable String id) {
        TeamMatch tm = leagueService.findOne(new TeamMatch(id));
        if (tm == null) {
            return Collections.emptyList();
        }
        Collection<PlayerResult> results = new ArrayList<>();
        if (tm.getSeason().isActive()) {
            results = leagueService.findCurrent(PlayerResult.class).parallelStream().filter(r->r.getTeamMatch().equals(tm)).collect(Collectors.toList());
        }  else {
            results = leagueService.findAll(PlayerResult.class).parallelStream().filter(r->r.getTeamMatch().equals(tm)).collect(Collectors.toList());
        }

        return results;
    }

    @RequestMapping(value = "/season/{id}/date", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Map<String,List<PlayerResult>> getPlayerResultSeasonByDate(Principal principal, @PathVariable String id) {
        Season s = leagueService.findOne(new Season(id));
        List<PlayerResult> results = new ArrayList<>();
        if (s.isActive()) {
            results = leagueService.findCurrent(PlayerResult.class).stream().parallel().filter(pr -> pr.getSeason().equals(s)).filter(PlayerResult::hasResults)
                    .collect(Collectors.toList());
        }
        results = leagueService.findAll(PlayerResult.class).stream().parallel()
                .filter(pr -> pr.getSeason().equals(s))
                .collect(Collectors.toList());

        return results.stream().collect(Collectors.groupingBy(pr->pr.getMatchDate().toLocalDate().toString()));
    }

    @RequestMapping(value = "/{matchId}/add", method = {RequestMethod.GET, RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<PlayerResult> addMatch(Principal principal, @PathVariable String matchId) {
        TeamMatch teamMatch = leagueService.findOne(new TeamMatch(matchId));
        List<PlayerResult> results = leagueService.findCurrent(PlayerResult.class)
                .parallelStream()
                .filter(pr -> pr.getTeamMatch().equals(teamMatch))
                .collect(Collectors.toList());
        results.add(resultService.add(teamMatch));
        return results;
    }

    @RequestMapping(value = "/{userId}/{seasonId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<PlayerResult> getUserResultsBySeason(Principal principal, @PathVariable String userId, @PathVariable String seasonId) {
        User u = leagueService.findOne(new User(userId));
        Season s = leagueService.findOne(new Season(seasonId));
        List<PlayerResult> results = new ArrayList<>(500);
        if (s == null || u == null) {
            return Collections.emptyList();
        }

        results = leagueService.findAll(PlayerResult.class)
                .stream()
                .parallel()
                .filter(pr -> pr.hasUser(u))
                .filter(pr -> pr.getSeason().equals(s))
                .filter(PlayerResult::hasResults)
                .collect(Collectors.toList());

        results.sort((playerResult, t1) -> t1.getMatchDate().compareTo(playerResult.getMatchDate()));
        if (s.isChallenge() || s.isNine()) {
            List<MatchPoints> matchPointsList = resultService.matchPoints();
            for (PlayerResult challengeResult : results) {
                challengeResult.setMatchPoints(
                        matchPointsList.parallelStream()
                                .filter(
                                        mp -> mp.getPlayerResult().getId().equals(challengeResult.getId()) &&
                                                mp.getUser().equals(u)
                                )
                                .findFirst().orElse(null));
            }
        }
        return results;
    }

    @JsonView(PlayerResultSummary.class)
    @RequestMapping(value = "/{userId}/{seasonId}/summary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<PlayerResult> getUserResultsBySeasonSummary(Principal principal, @PathVariable String userId, @PathVariable String seasonId) {
        return getUserResultsBySeason(principal,userId,seasonId);
    }


}
