package com.society.leagues.resource;

import com.society.leagues.service.LeagueService;
import com.society.leagues.service.ResultService;
import com.society.leagues.service.StatService;
import com.society.leagues.client.api.domain.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/stat")
@SuppressWarnings("unused")
public class StatResource {
    @Autowired LeagueService leagueService;
    @Autowired StatService statService;
    @Autowired ResultService resultService;

    final static Logger logger = Logger.getLogger(StatResource.class);

    @RequestMapping(value = "/team/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public Stat getTeamStat(@PathVariable String id) {
        Team team = new Team(id);
        return statService.getTeamStats().parallelStream().filter(s->s.getTeam().equals(team)).findFirst().orElse(null);
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

        return statService.getUserSeasonStats().get(team.getSeason()).stream().parallel().filter(s -> team.hasUser(s.getUser()))
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/season/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getSeasonStats(@PathVariable String id) {
        Season season = leagueService.findOne(new Season(id));
        List<Stat> stats = statService.getTeamStats().parallelStream().filter(s->s.getSeason().equals(season)).sorted(new Comparator<Stat>() {
            @Override
            public int compare(Stat stat, Stat t1) {
                if (!Objects.equals(t1.getWins(), stat.getWins())) {
                    return t1.getWins().compareTo(stat.getWins());
                }
                if (!Objects.equals(t1.getLoses(), stat.getLoses())) {
                    return stat.getLoses().compareTo(t1.getLoses());
                }
                if (stat.getSeason().isNine()) {

                }
                if (!Objects.equals(t1.getRacksWon(), stat.getRacksWon())) {
                    return t1.getRacksWon().compareTo(stat.getRacksWon());
                }
                return stat.getRacksLost().compareTo(t1.getRacksLost());
            }
        }).collect(Collectors.toList());
        List<MatchPoints> points = resultService.matchPoints();
        if (!season.isChallenge())
            return stats;

        for (Stat stat : stats) {
            double totalPoints = 0d;
            User u = stat.getTeam().getChallengeUser();
            List<MatchPoints> pointsList = points.stream().parallel().filter(p->p.getUser().equals(u)).collect(Collectors.toList());
            for (MatchPoints matchPoints : pointsList) {
                totalPoints += matchPoints.getWeightedAvg();
            }
            stat.setPoints(totalPoints);
        }
        return stats;
    }

    @RequestMapping(value = "/matchpoints", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Map<String,MatchPoints> matchPoints() {
        LocalDateTime tenWeeksAgo = LocalDateTime.now().minusWeeks(10);
        HashMap<String,MatchPoints> hashMap = new HashMap<>(50);
        for(MatchPoints points: resultService.matchPoints()) {
            hashMap.put(points.getPlayerResult().getId(), points);
        }
        return hashMap;
    }

     @RequestMapping(value = "/season/players/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getSeasonPlayerStats(@PathVariable String id) {
         final Season season = leagueService.findOne(new Season(id));
         List<Team> teams = leagueService.findAll(Team.class).stream().parallel().filter(t->t.getSeason().equals(season)).collect(Collectors.toList());
         List<PlayerResult> results = leagueService.findAll(PlayerResult.class).stream().parallel().
                 filter(pr -> pr.getSeason().equals(season)).
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
             Team team = teams.stream().filter(t -> t.getMembers().contains(user)).findFirst().orElse(null);
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
        List<Stat> userStats = new ArrayList<>();

        statService.getUserSeasonStats().values().stream().parallel().forEach(new Consumer<List<Stat>>() {
            @Override
            public void accept(List<Stat> stats) {
                stats.parallelStream().filter(st -> st.getUser().equals(u)).forEach(userStats::add);
            }
        });
        userStats.add(Stat.buildLifeTimeStats(u, userStats));
        return userStats;
    }

    @RequestMapping(value = "/user/{id}/{seasonId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getUserStatsSeason(@PathVariable String id, @PathVariable String seasonId) {
        User u = leagueService.findOne(new User(id));
        return statService.getUserSeasonStats()
                .get(new Season(seasonId)).stream().parallel().filter(s->s.getUser().equals(u)).collect(Collectors.toList());
    }
}
