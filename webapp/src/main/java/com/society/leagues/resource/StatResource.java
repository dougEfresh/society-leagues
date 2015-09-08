package com.society.leagues.resource;

import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/stat")
public class StatResource {

    @Autowired LeagueService leagueService;

    @RequestMapping(value = "/team/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public Stat getTeamStat(@PathVariable String id) {
        Team team = leagueService.findOne(new Team(id));
        if (team == null) {
            return new Stat();
        }
        List<TeamMatch> matches = leagueService.findTeamMatchByTeam(team).stream().filter(tm->tm.hasResults()).collect(Collectors.toList());
        return Stat.buildTeamStats(team, matches);
    }

    @RequestMapping(value = "/team/{id}/members",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getTeamMemberStats(@PathVariable String id) {
        Team team = leagueService.findOne(new Team(id));
        if (team == null) {
            return Collections.emptyList();
        }
        return Stat.buildTeamMemberStats(team, leagueService.findPlayerResultBySeason(team.getSeason()));
    }

    @RequestMapping(value = "/season/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getSeasonStats(@PathVariable String id) {
        Season season = leagueService.findOne(new Season(id));
        List<Team> teams = leagueService.findAll(Team.class).stream().filter(t->t.getSeason().equals(season)).collect(Collectors.toList());
        List<Stat> stats = new ArrayList<>(100);
        for (Team team : teams) {
            List<TeamMatch> matches = leagueService.findTeamMatchByTeam(team).stream().filter(tm->tm.hasResults()).collect(Collectors.toList());
            stats.add(Stat.buildTeamStats(team, matches));
        }
        return stats;
    }

     @RequestMapping(value = "/season/players/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getSeasonPlayerStats(@PathVariable String id) {
         Season season = leagueService.findOne(new Season(id));
         List<Team> teams = leagueService.findTeamBySeason(season);
         List<PlayerResult> results = leagueService.findPlayerResultBySeason(season).stream().
                 filter(r -> r.getLoser() != null && r.getWinner() != null).
                 collect(Collectors.toList());
         Map<User,List<PlayerResult>> losers = results.stream().collect(Collectors.groupingBy(r -> r.getLoser(), Collectors.toList()));
         Map<User,List<PlayerResult>> winners = results.stream().collect(Collectors.groupingBy(r -> r.getWinner(),Collectors.toList()));
         Map<User,List<PlayerResult>> all = new HashMap<>();
         for (User user : winners.keySet()) {
             all.put(user,winners.get(user));
         }
         for (User user : losers.keySet()) {
             if (all.containsKey(user)) {
                 all.get(user).addAll(losers.get(user));
             } else {
                 all.put(user, losers.get(user));
             }
         }
         List<Stat> stats = new ArrayList<>(100);
         for (User user : all.keySet()) {
             Team team = teams.stream().filter(t->t.getMembers().contains(user)).findFirst().orElse(null);
             if (team == null) {
                 continue;
             }
             stats.add(Stat.buildPlayerTeamStats(user,
                             team,
                     all.get(user))
             );
         }
         return stats.stream().sorted(new Comparator<Stat>() {
             @Override
             public int compare(Stat stat, Stat t1) {
                 return t1.getWinPct().compareTo(stat.getWinPct());
             }
         }).collect(Collectors.toList());
    }

    @RequestMapping(value = "/user/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getUserStats(@PathVariable String id) {
        User u = leagueService.findOne(new User(id));
        List<Stat> stats = new ArrayList<>(100);
        List<Team> teams = leagueService.findAll(Team.class).stream().
                filter(t -> t.getMembers().contains(u)).
                filter(t -> t.getSeason().isActive()).
                collect(Collectors.toList());

        List<PlayerResult> results = leagueService.findPlayerResultByUser(u).stream().
                filter(r -> r.getSeason().isActive()).
                collect(Collectors.toList());
        for (Team team : teams) {
            stats.add(Stat.buildPlayerTeamStats(
                            u,
                            team,
                            results.stream().filter(r -> r.hasTeam(team)).collect(Collectors.toList())
                    )
            );
        }
        stats.add(Stat.buildStats(u, stats));
        return stats;
    }
}
