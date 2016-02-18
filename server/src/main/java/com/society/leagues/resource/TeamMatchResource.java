package com.society.leagues.resource;

import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.views.PlayerResultSummary;
import com.society.leagues.exception.ChallengeException;
import com.society.leagues.exception.InvalidRequestException;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.ResultService;
import com.society.leagues.service.StatService;
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
    @Autowired StatService statService;

    @RequestMapping(value = "/admin/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch create(@RequestBody TeamMatch teamMatch) {
        return leagueService.save(teamMatch);
    }

    @RequestMapping(value = "/admin/delete/{teamMatchId}", method = {RequestMethod.GET , RequestMethod.DELETE}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Map<String,List<TeamMatch>> delete(Principal principal, @PathVariable String teamMatchId) {
        TeamMatch tm = leagueService.findOne(new TeamMatch(teamMatchId));
        if (tm == null) {
            throw new InvalidRequestException("Could not find team match for " + teamMatchId);
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

    @RequestMapping(value = "/admin/modify/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<TeamMatch> modify(@RequestBody List<TeamMatch> teamMatch) {
        List<TeamMatch> processed = new ArrayList<>(teamMatch.size());
        processed.addAll(teamMatch.stream().map(this::modify).collect(Collectors.toList()));
        logger.info("Refreshing team stats");
        for (TeamMatch match : processed) {
            statService.refreshTeamStats(match.getHome());
            statService.refreshTeamStats(match.getAway());
        }
        if (processed.get(0).getSeason().isChallenge()) {
            statService.refresh();
        }
        return processed;
    }

    @RequestMapping(value = "/admin/delete/playerMatches/{teamMatchId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch deletePlayerMatches(@PathVariable String teamMatchId) {
        TeamMatch teamMatch = leagueService.findOne(new TeamMatch(teamMatchId));
        if (teamMatch == null)
            return new TeamMatch(teamMatchId);

        leagueService.findAll(PlayerResult.class).parallelStream().filter(p->p.getTeamMatch().equals(teamMatch)).forEach( p->
                leagueService.purge(p)
        );

        return teamMatch;
    }


    @RequestMapping(value = "/admin/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch modify(@RequestBody TeamMatch teamMatch) {
        TeamMatch t = modifyNoSave(teamMatch);
        return leagueService.save(t);
    }

    private TeamMatch modifyNoSave(TeamMatch teamMatch) {
        TeamMatch existing;
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
            existing = leagueService.save(existing);
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
        final String existingId = existing.getId();
        boolean hasPlayerResults = leagueService.findCurrent(PlayerResult.class).parallelStream()
                .filter(r->r.hasResults())
                .filter(r->r.getTeamMatch().getId().equals(existingId)).count() > 0;
        existing.setHasPlayerResults(hasPlayerResults);
        existing.setHomeForfeits(teamMatch.getHomeForfeits());
        existing.setAwayForfeits(teamMatch.getAwayForfeits());
        return existing;
    }

    @RequestMapping(value = "/admin/add/{seasonId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch add(Principal principal, @PathVariable String seasonId) {
        return add(principal,seasonId,LocalDate.now().toString());
    }

    @RequestMapping(value = "/admin/add/{seasonId}/{date}", method = {RequestMethod.GET, RequestMethod.PUT}, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public TeamMatch add(Principal principal, @PathVariable String seasonId, @PathVariable String date) {
        TeamMatch tm = new TeamMatch();
        tm.setMatchDate(LocalDate.parse(date).atTime(11,0));
        tm.setHome(leagueService.findAll(Team.class).stream().filter(t -> t.getSeason().getId().equals(seasonId)).collect(Collectors.toList()).get(0));
        tm.setAway(leagueService.findAll(Team.class).stream().filter(t -> t.getSeason().getId().equals(seasonId)).collect(Collectors.toList()).get(1));
        return leagueService.save(tm);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public TeamMatch get(Principal principal, @PathVariable String id) {
        return leagueService.findOne(new TeamMatch(id));
    }

    @RequestMapping(value = "/members/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public Map<String,Set<User>> getTeamMatchMembers(Principal principal, @PathVariable String id) {
        TeamMatch tm = leagueService.findOne(new TeamMatch(id));
        if (tm == null)
            return Collections.emptyMap();

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
        results = leagueService.findAll(TeamMatch.class).stream().parallel()
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


    @JsonView(PlayerResultSummary.class)
    @RequestMapping(value = {"/season/{id}/summary"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Map<String,List<TeamMatch>> getTeamMatchesSummaryByDate(Principal principal, @PathVariable String id) {
        return getTeamMatches(principal,id);
    }

    @JsonView(PlayerResultSummary.class)
    @RequestMapping(value = {"/season/{id}/summary/list"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<TeamMatch> getTeamMatchesSummary(Principal principal, @PathVariable String id) throws Exception {
        Season season = leagueService.findOne(new Season(id));
        if (season == null) {
            throw new InvalidRequestException("Could not find season for " + id);
        }
        return leagueService.findAll(TeamMatch.class)
                .parallelStream()
                .filter(t->t.getHome().getSeason().equals(season))
                .filter(t->t.getAway().getSeason().equals(season))
                .collect(Collectors.toList());
    }

  @RequestMapping(value = {"/team/{teamId}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
  public List<TeamMatch> getMatchesByTeam(Principal principal, @PathVariable String teamId) {
      List<TeamMatch> results;
      Team team = leagueService.findOne(new Team(teamId));
      LocalDateTime  now  = LocalDateTime.now().minusDays(1);
      results = leagueService.findCurrent(TeamMatch.class).stream().parallel()
                .filter(tm -> tm.hasTeam(team))
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
        return results;
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

    @RequestMapping(value = "/modify/available", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public TeamMatch modifyAvailable(@RequestBody TeamMatch teamMatch) {
        TeamMatch existing = leagueService.findOne(teamMatch);
        if (existing == null) {
            throw new InvalidRequestException("Unknown teamMatch id " + teamMatch.getId());
        }
        existing.getHomeNotAvailable().clear();
        existing.getAwayNotAvailable().clear();

        teamMatch.getHomeNotAvailable().forEach(existing::addHomeNotAvailable);
        teamMatch.getAwayNotAvailable().forEach(existing::addAwayNotAvailable);

        return leagueService.save(existing);
    }

}
