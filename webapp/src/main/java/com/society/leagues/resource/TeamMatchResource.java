package com.society.leagues.resource;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.ResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/teammatch")
@SuppressWarnings("unused")
public class TeamMatchResource {
    static Logger logger = LoggerFactory.getLogger(TeamMatchResource.class);
    @Autowired LeagueService leagueService;
    @Autowired ResultService resultService;

    @RequestMapping(value = "/admin/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch create(@RequestBody TeamMatch teamMatch) {
        return leagueService.save(teamMatch);
    }

    @RequestMapping(value = "/admin/delete/{teamMatchId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Map<String,List<TeamMatch>> delete(Principal principal, @PathVariable String teamMatchId) {
        TeamMatch tm = leagueService.findOne(new TeamMatch(teamMatchId));
        if (tm == null) {
            logger.error("Could not find team match for " + teamMatchId);
            return Collections.emptyMap();
        }
        resultService.removeTeamMatchResult(tm);
        return Collections.emptyMap();
    }

    @RequestMapping(value = "/admin/create/{seasonId}/{date}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Map<String,List<TeamMatch>> createTemplate(Principal principal, @PathVariable String seasonId, @PathVariable String date ) {
        Season s = new Season(seasonId);
        LocalDateTime dt = LocalDate.parse(date).atStartOfDay();
        //TODO what if there are no matches yet ?
        TeamMatch teamMatch =  leagueService.findAll(TeamMatch.class).parallelStream().filter(tm -> tm.getSeason().equals(s)).findAny().orElse(null);
        if (teamMatch == null)
            return null;

        TeamMatch copy = TeamMatch.copy(teamMatch);
        copy.setId(null);
        copy.setHomeRacks(0);
        copy.setAwayRacks(0);
        copy.setSetAwayWins(0);
        copy.setSetHomeWins(0);
        copy.setMatchDate(dt.withHour(11));
        //leagueService.save(copy);
        return getTeamMatchSeason(principal, s.getId(), "upcoming");
    }

    @RequestMapping(value = "/admin/modify/{teamMatchId}/team/{type}/{teamId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch modifyTeam(Principal principal,
                                @PathVariable String teamMatchId,
                                @PathVariable String type,
                                @PathVariable String teamId) {
        TeamMatch teamMatch = leagueService.findOne(new TeamMatch(teamId));
        Team team = leagueService.findOne(new Team(teamId));
        if (type.equals("winner")) {
            if (teamMatch.getWinner().equals(teamMatch.getHome())) {
                teamMatch.setHome(team);
            } else {
                teamMatch.setAway(team);
            }
        } else {
            if (teamMatch.getLoser().equals(teamMatch.getHome())) {
                teamMatch.setHome(team);
            } else {
                teamMatch.setAway(team);
            }
        }
        return leagueService.save(teamMatch);
    }
    @RequestMapping(value = "/admin/modify/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<TeamMatch> modify(@RequestBody List<TeamMatch> teamMatch) {
        List<TeamMatch> processed = new ArrayList<>(teamMatch.size());
        processed.addAll(teamMatch.stream().map(this::modify).collect(Collectors.toList()));
        leagueService.save(processed);

        return processed;
    }

    private TeamMatch modifyNoSave(TeamMatch teamMatch) {
         final  TeamMatch existing;
        if  (leagueService.findOne(teamMatch) == null)
            existing = new TeamMatch();
        else
            existing = leagueService.findOne(teamMatch);

        existing.setHome(leagueService.findOne(teamMatch.getHome()));
        existing.setAway(leagueService.findOne(teamMatch.getAway()));
        existing.setAwayRacks(teamMatch.getAwayRacks());
        existing.setHomeRacks(teamMatch.getHomeRacks());
        existing.setSetAwayWins(teamMatch.getSetAwayWins());
        existing.setSetHomeWins(teamMatch.getSetHomeWins());
        existing.setMatchDate(teamMatch.getMatchDate());

        if (existing.getSeason().isScramble()) {
            if (teamMatch.getDivision() != null)
                existing.setDivision(teamMatch.getDivision());
        }

        if (existing.getId() == null){
            leagueService.save(existing);
        }

        if (existing.getSeason().isChallenge()) {
            PlayerResult result = leagueService.findAll(PlayerResult.class).stream().parallel().filter(p -> p.getTeamMatch().equals(teamMatch)).findFirst().orElse(null);
            if (result == null) {
                result = new PlayerResult();
            }
            result.setTeamMatch(existing);
            result.setPlayerHome(existing.getHome().getChallengeUser());
            result.setPlayerHomeHandicap(existing.getHome().getChallengeUser().getHandicap(existing.getSeason()));

            result.setPlayerAway(existing.getAway().getChallengeUser());
            result.setPlayerAwayHandicap(existing.getAway().getChallengeUser().getHandicap(existing.getSeason()));

            result.setHomeRacks(existing.getHomeRacks());
            result.setAwayRacks(existing.getAwayRacks());

            leagueService.save(result);
        }
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        boolean hasPlayerResults = leagueService.findCurrent(PlayerResult.class).parallelStream().filter(r->r.getTeamMatch().equals(existing)).count() > 0;
        existing.setHasPlayerResults(hasPlayerResults);
        existing.setHomeForfeits(teamMatch.getHomeForfeits());
        existing.setAwayForfeits(teamMatch.getAwayForfeits());
        return existing;
    }

    @RequestMapping(value = "/admin/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch modify(@RequestBody TeamMatch teamMatch) {
        TeamMatch t = modifyNoSave(teamMatch);
        return leagueService.save(t);
    }


    @RequestMapping(value = "/admin/add/{seasonId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch add(Principal principal, @PathVariable String seasonId) {
        return add(principal,seasonId,LocalDate.now().toString());
    }

    @RequestMapping(value = "/admin/add/{seasonId}/{date}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch add(Principal principal, @PathVariable String seasonId, @PathVariable String date) {
        TeamMatch tm = new TeamMatch();
        tm.setMatchDate(LocalDate.parse(date).atTime(11,0));
        tm.setHome(leagueService.findAll(Team.class).stream().filter(t -> t.getSeason().getId().equals(seasonId)).findFirst().get());
        tm.setAway(leagueService.findAll(Team.class).stream().filter(t -> t.getSeason().getId().equals(seasonId)).findFirst().get());
        return leagueService.save(tm);
    }

    @RequestMapping(value = "/admin/change/home/{teamMatchId}/{teamId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch changeHomeTeam(@PathVariable String teamMatchId,  @PathVariable String teamId) {
        TeamMatch tm = leagueService.findOne(new TeamMatch(teamMatchId));
        if (tm == null){
            return null;
        }
        Team t = leagueService.findOne(new Team(teamId));
        tm.setHome(t);
        leagueService.findAll(PlayerResult.class).stream().filter(p->p.getTeamMatch().equals(tm)).forEach(leagueService::purge);
        return leagueService.save(tm);
    }

    @RequestMapping(value = "/admin/change/away/{teamMatchId}/{teamId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch changeAwayTeam(@PathVariable String teamMatchId,  @PathVariable String teamId) {
        TeamMatch tm = leagueService.findOne(new TeamMatch(teamMatchId));
        if (tm == null){
            return null;
        }
        Team t = leagueService.findOne(new Team(teamId));
        tm.setAway(t);
        leagueService.findAll(PlayerResult.class).stream().filter(p->p.getTeamMatch().equals(tm)).forEach(leagueService::purge);
        return leagueService.save(tm);
    }

    @RequestMapping(value = "/admin/change/date/{teamMatchId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch changeDate(@RequestParam String date  , @PathVariable String teamMatchId) {
        TeamMatch tm = leagueService.findOne(new TeamMatch(teamMatchId));
        if (tm == null){
            return null;
        }
        LocalDateTime dt = LocalDateTime.parse(date);
        tm.setMatchDate(dt);
        return leagueService.save(tm);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public TeamMatch get(Principal principal, @PathVariable String id) {
        return leagueService.findOne(new TeamMatch(id));
    }

    @RequestMapping(value = "/racks/{teamMatchId}/{teamId}/{racks}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch updateRacks(Principal principal, @PathVariable String teamMatchId, @PathVariable String teamId, @PathVariable Integer racks) {
        TeamMatch  teamMatch = leagueService.findOne(new TeamMatch(teamMatchId));
        Team team = leagueService.findOne(new Team(teamId));
        PlayerResult result = leagueService.findAll(PlayerResult.class).stream().parallel().filter(p -> p.getTeamMatch().equals(teamMatch)).findFirst().orElse(null);

        if (teamMatch.getSeason().isChallenge()) {
            if (result == null) {
                result = new PlayerResult();
                result.setTeamMatch(teamMatch);
                result.setPlayerHome(team.getChallengeUser());
                result.setPlayerHomeHandicap(team.getChallengeUser().getHandicap(team.getSeason()));
                result.setPlayerAway(teamMatch.getAway().getChallengeUser());
                result.setPlayerAwayHandicap(teamMatch.getAway().getChallengeUser().getHandicap(team.getSeason()));
            }
        }
        if (teamMatch.getHome().equals(team)) {
            teamMatch.setHomeRacks(racks);
            result.setHomeRacks(racks);
        } else {
            teamMatch.setAwayRacks(racks);
            result.setAwayRacks(racks);
        }
        if (teamMatch.isChallenge())
            leagueService.save(result);

        return leagueService.save(teamMatch);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @RequestMapping(value = "/wins/{teamMatchId}/{teamId}/{wins}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public TeamMatch updateWins(Principal principal, @PathVariable String teamMatchId, @PathVariable String teamId, @PathVariable Integer wins) {
        TeamMatch  teamMatch = leagueService.findOne(new TeamMatch(teamMatchId));
        Team team = leagueService.findOne(new Team(teamId));
        if (teamMatch.getHome().equals(team)) {
            teamMatch.setSetHomeWins(wins);
        } else {
            teamMatch.setSetAwayWins(wins);
        }
        return leagueService.save(teamMatch);
    }

    @RequestMapping(value = "/members/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public Map<String,Set<User>> getTeamMatchMembers(Principal principal, @PathVariable String id) {
        TeamMatch tm = leagueService.findOne(new TeamMatch(id));
        Map<String,Set<User>> members = new HashMap<>();
        members.put("home",tm.getHome().getMembers().getMembers());
        members.put("away",tm.getAway().getMembers().getMembers());
        return members;
    }

    @RequestMapping(value = {"/season/{id}/{type}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Map<String,List<TeamMatch>> getTeamMatchSeason(Principal principal, @PathVariable String id, @PathVariable String type) {
        Season s = leagueService.findOne(new Season(id));
        List<TeamMatch> results;
        Predicate<TeamMatch> filter;
        LocalDateTime  now  = LocalDateTime.now().minusDays(1);
        if (type.equals("upcoming")) {
            filter = teamMatch -> teamMatch.getMatchDate().isAfter(now);
        } else if (type.equals("pending")) {
            filter = teamMatch -> teamMatch.getMatchDate().isBefore(now) && !teamMatch.isHasResults();
        } else {
            filter = teamMatch -> teamMatch.isHasResults();
        }
        results = leagueService.findCurrent(TeamMatch.class).stream().parallel()
                .filter(tm -> tm.getSeason().equals(s))
                .filter(filter)
                .sorted(new Comparator<TeamMatch>() {
                    @Override
                    public int compare(TeamMatch teamMatch, TeamMatch t1) {
                        return t1.getMatchDate().compareTo(teamMatch.getMatchDate());
                    }
                }).collect(Collectors.toList());
        Map<String,List<TeamMatch>> group = results.stream().collect(Collectors.groupingBy(tm -> tm.getMatchDate().toLocalDate().toString()));
        return (Map<String,List<TeamMatch>>) new TreeMap<>(group);
    }

    @RequestMapping(value = {"/season/{id}/all"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Map<String,List<TeamMatch>> getTeamMatches(Principal principal, @PathVariable String id) {
        Season s = leagueService.findOne(new Season(id));
        List<TeamMatch> results;
        LocalDateTime  now  = LocalDateTime.now().minusDays(1);
        results = leagueService.findCurrent(TeamMatch.class).stream().parallel()
                .filter(tm -> tm.getSeason().equals(s))
                .sorted(new Comparator<TeamMatch>() {
                    @Override
                    public int compare(TeamMatch teamMatch, TeamMatch t1) {
                        if (t1.getMatchDate() == null || teamMatch.getMatchDate() == null)
                            return -1;
                        return t1.getMatchDate().compareTo(teamMatch.getMatchDate());
                    }
                }).collect(Collectors.toList());
        for (TeamMatch result : results) {
            boolean hasPlayerResults = leagueService.findCurrent(PlayerResult.class).parallelStream().filter(r->r.getTeamMatch().equals(result)).count() > 0;
            result.setHasPlayerResults(hasPlayerResults);
        }
        Map<String,List<TeamMatch>> group = results.stream().collect(Collectors.groupingBy(tm -> tm.getMatchDate().toLocalDate().toString()));

        return (Map<String,List<TeamMatch>>) new TreeMap<>(group);
    }


    @RequestMapping(value = "/user/{id}/{type}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<TeamMatch> getTeamMatchUser(Principal principal, @PathVariable String id, @PathVariable String type) {
        User u = leagueService.findOne(new User(id));
        List<Team> userTeams = leagueService.findAll(Team.class).stream().
                filter(tm -> tm.getMembers().getMembers().contains(u)).filter(t -> t.getSeason().isActive())
                .collect(Collectors.toList());
        List<TeamMatch> teamMatches = new ArrayList<>();
        for (Team userTeam : userTeams) {
            teamMatches.addAll(leagueService.findAll(TeamMatch.class).stream().filter(tm->tm.hasTeam(userTeam)).collect(Collectors.toList()));
        }
        List<TeamMatch> copy = new ArrayList<>(teamMatches.size());
        teamMatches.stream().forEach(tm->copy.add(LeagueObject.copy(tm)));
        copy.parallelStream().forEach(c->c.setReferenceUser(u));
        return copy;
    }
}
