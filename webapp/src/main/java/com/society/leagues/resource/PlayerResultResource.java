package com.society.leagues.resource;


import com.fasterxml.jackson.annotation.JsonView;
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

    @RequestMapping(value = "/admin/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PlayerResult create(@RequestBody PlayerResult playerResult) {
        return resultService.createOrModify(playerResult);
    }

    @RequestMapping(value = "/admin/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PlayerResult modify(@RequestBody PlayerResult playerResult) {
        return resultService.createOrModify(playerResult);
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

    @RequestMapping(value = "/teammatch/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @JsonView(PlayerResultView.class)
    public Collection<PlayerResult> getPlayerResultTeamMatch(Principal principal, @PathVariable String id) {
        TeamMatch tm = leagueService.findOne(new TeamMatch(id));
        Collection<PlayerResult> results;
        if (tm.getSeason().isActive()) {
            results = leagueService.findCurrent(PlayerResult.class);
        }  else {
            results = leagueService.findAll(PlayerResult.class);
        }

        results = results.stream().parallel().
                filter(pr->pr.getTeamMatch().equals(tm))
                .filter(pr->!pr.getLoser().isFake())
                .filter(pr->!pr.getWinner().isFake())
                .sorted(new Comparator<PlayerResult>() {
                    @Override
                    public int compare(PlayerResult playerResult, PlayerResult t1) {
                        return playerResult.getMatchNumber().compareTo(t1.getMatchNumber());
                    }
        }).collect(Collectors.toList());

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

    @RequestMapping(value = "/get/season/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @JsonView(PlayerResultView.class)
    public List<PlayerResult> getPlayerResultSeason(Principal principal, @PathVariable String id) {
        Season s = leagueService.findOne(new Season(id));
        if (s.isActive()) {
            return leagueService.findCurrent(PlayerResult.class).stream().parallel().filter(pr -> pr.getSeason().equals(s))
                .collect(Collectors.toList());
        }
        return leagueService.findAll(PlayerResult.class).stream().parallel()
                .filter(pr -> pr.getSeason().equals(s))
                .collect(Collectors.toList());
    }


    @RequestMapping(value = "/season/{id}/date", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @JsonView(PlayerResultView.class)
    public Map<String,List<PlayerResult>> getPlayerResultSeasonByDate(Principal principal, @PathVariable String id) {
        Season s = leagueService.findOne(new Season(id));
        List<PlayerResult> results = new ArrayList<>();
        if (s.isActive()) {
            results = leagueService.findCurrent(PlayerResult.class).stream().parallel().filter(pr -> pr.getSeason().equals(s))
                .collect(Collectors.toList());
        }
        results = leagueService.findAll(PlayerResult.class).stream().parallel()
                .filter(pr -> pr.getSeason().equals(s))
                .collect(Collectors.toList());

        return results.stream().collect(Collectors.groupingBy(pr->pr.getMatchDate().toLocalDate().toString()));
    }



    @RequestMapping(value = {"/team/{id}","/get/team/{id}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @JsonView(PlayerResultView.class)
    public List<PlayerResult> getPlayerResultTeam(Principal principal, @PathVariable String id) {
        Team t  = leagueService.findOne(new Team(id));
        List<PlayerResult> results;
        if (t.getSeason().isActive()) {
            results = leagueService.findCurrent(PlayerResult.class).stream().parallel()
                    .filter(pr -> pr.getSeason().equals(t.getSeason()))
                    .filter(pr -> pr.hasTeam(t)).collect(Collectors.toList());
        } else {
            results = leagueService.findAll(PlayerResult.class).stream().parallel()
                    .filter(pr -> pr.getSeason().equals(t.getSeason()))
                    .filter(pr -> pr.hasTeam(t)).collect(Collectors.toList());
        }
        final List<PlayerResult> copyResults = new ArrayList<>(results.size());
        results.stream().forEach(r-> copyResults.add(PlayerResult.copy(r)));
        copyResults.parallelStream().forEach(pr -> pr.setReferenceTeam(t));
        return copyResults.stream().
                sorted((playerResult, t1) -> playerResult.getTeamMember().getName().compareTo(t1.getTeamMember().getName())).
                collect(Collectors.toList());

    }

    @RequestMapping(value = "/get/user/{id}/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @JsonView(value = {PlayerResultView.class})
    public Map<String,List<PlayerResult>> getPlayerResultByUser(Principal principal, @PathVariable String id, @PathVariable String type) {
        User u = leagueService.findOne(new User(id));
        List<PlayerResult> results = new ArrayList<>(500);
        if (type.equals("all"))
         results = leagueService.findAll(PlayerResult.class).stream().parallel().filter(pr -> pr.hasUser(u))
                    .sorted((playerResult, t1) -> t1.getMatchDate().compareTo(playerResult.getMatchDate()))
                    .collect(Collectors.toList());
        else
            results = leagueService.findCurrent(PlayerResult.class).stream().
                    parallel().filter(pr->pr.hasUser(u)).collect(Collectors.toList());


        List<PlayerResult> copyResults = new ArrayList<>(results.size());
        results.stream().forEach(r-> copyResults.add(PlayerResult.copy(r)));
        copyResults.parallelStream().forEach(pr -> pr.setReferenceUser(u));
        Map<String,List<PlayerResult>> resultsBySeason = copyResults.stream().collect(Collectors.groupingBy(pr -> pr.getSeason().getId()));

        for (String season : resultsBySeason.keySet()) {
            resultsBySeason.put(season, resultsBySeason.get(season).stream()
                    .sorted((playerResult, t1)
                                    -> t1.getMatchDate().compareTo(playerResult.getMatchDate())
                    )
                .collect(Collectors.toList()));
        }


        if (u.isChallenge()) {
            List<MatchPoints> matchPointsList = resultService.matchPoints();
            List<PlayerResult> challengeResults = resultsBySeason.get(u.getHandicapSeasons().stream().filter(s->s.getSeason().isChallenge()).findFirst().get().getSeason().getId());
            for (PlayerResult challengeResult : challengeResults) {
                challengeResult.setMatchPoints(
                        matchPointsList.parallelStream()
                                .filter(
                                        mp -> mp.getPlayerResult().getId().equals(challengeResult.getId()) &&
                                                mp.getUser().equals(u)
                                )
                                .findFirst().orElse(null));
            }
        }
        return resultsBySeason;
    }


    @RequestMapping(value = "/user/{id}/{seasonId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @JsonView(value = {PlayerResultView.class})
    public Map<String,Object> getPlayerResultByUserSeason(Principal principal, @PathVariable String id, @PathVariable String seasonId) {
        User u = leagueService.findOne(new User(id));
        Season s = leagueService.findOne(new Season(seasonId));
        List<PlayerResult> results = new ArrayList<>(500);
        if (s == null) {
            return Collections.EMPTY_MAP;
        }

        results = leagueService.findAll(PlayerResult.class)
                .stream().parallel().filter(pr -> pr.hasUser(u)).filter(pr->pr.getSeason().equals(s)).collect(Collectors.toList());

        List<PlayerResult> copyResults = new ArrayList<>(results.size());
        results.stream().forEach(r-> copyResults.add(PlayerResult.copy(r)));
        copyResults.parallelStream().forEach(pr -> pr.setReferenceUser(u));
        copyResults.sort(
                (playerResult, t1) -> t1.getMatchDate().compareTo(playerResult.getMatchDate())
        );

        if (s.isChallenge()) {
            List<MatchPoints> matchPointsList = resultService.matchPoints();
            for (PlayerResult challengeResult : copyResults) {
                challengeResult.setMatchPoints(
                        matchPointsList.parallelStream()
                                .filter(
                                        mp -> mp.getPlayerResult().getId().equals(challengeResult.getId()) &&
                                                mp.getUser().equals(u)
                                )
                                .findFirst().orElse(null));
            }
        }
        Map<String,Object> r = new HashMap<>();
        r.put("stats",statService.getUserSeasonStats().get(s).parallelStream().filter(st->st.getUser().equals(u)).findFirst().orElse(null));
        r.put("results",copyResults);
        return r;
    }

    @RequestMapping(value = "/racks/{matchId}/{type}/{racks}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PlayerResult updateRacks(Principal principal, @PathVariable String matchId, @PathVariable String type, @PathVariable Integer racks) {
        PlayerResult result = leagueService.findOne(new PlayerResult(matchId));
        if (result == null)
            return null;

        if (type.equals("home")) {
            result.setHomeRacks(racks);
        } else {
            result.setAwayRacks(racks);
        }

        return leagueService.save(result);
    }


    @RequestMapping(value = "/player/{matchId}/{type}/{playerId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PlayerResult updatePlayer(Principal principal, @PathVariable String matchId, @PathVariable String type, @PathVariable String playerId) {
        PlayerResult result = leagueService.findOne(new PlayerResult(matchId));
        User player = leagueService.findOne(new User(playerId));
        if (result == null || player == null)
            return null;

        if (type.equals("home")) {
            result.setPlayerHome(player);
        } else {
            result.setPlayerAway(player);
        }

        return leagueService.save(result);
    }

    @RequestMapping(value = "/{matchId}/add", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
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


    @RequestMapping(value = "/{resultId}/winner/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PlayerResult setWinner(Principal principal, @PathVariable String resultId, @PathVariable String userId) {
        PlayerResult result = leagueService.findOne(new PlayerResult(resultId));
        User user = leagueService.findOne(new User(userId));
        if (result == null || user == null)
            throw new RuntimeException("No user or reulst for " + userId + " "+ resultId);

        if (result.getPlayerHome().equals(user)) {
            result.setHomeRacks(1);
            result.setAwayRacks(0);
        } else {
            result.setHomeRacks(0);
            result.setAwayRacks(1);
        }
        return leagueService.save(result);
    }

    @RequestMapping(value = "/{resultId}/loser/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public PlayerResult setLoser(Principal principal, @PathVariable String resultId, @PathVariable String userId) {
        PlayerResult result = leagueService.findOne(new PlayerResult(resultId));
        User user = leagueService.findOne(new User(userId));
        if (result == null || user == null)
            throw new RuntimeException("No user or result for " + userId + " "+ resultId);

        if (result.getPlayerHome().equals(user)) {
            result.setHomeRacks(0);
            result.setAwayRacks(1);
        } else {
            result.setHomeRacks(1);
            result.setAwayRacks(0);
        }
        return leagueService.save(result);
    }



}
