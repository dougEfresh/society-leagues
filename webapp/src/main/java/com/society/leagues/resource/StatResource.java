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

        List<Stat> stats = statService.getUserSeasonStats().get(team.getSeason()).stream().parallel().filter(s -> team.hasUser(s.getUser()))
                .collect(Collectors.toList());
        for (User user : team.getMembers()) {
            if (stats.stream().filter(s->s.getUser().equals(user)).count() == 0) {
                Stat stat = new Stat();
                stat.setTeam(team);
                stat.setUser(user);
                stat.setSeason(team.getSeason());
                stats.add(stat);
            }
        }
        return stats;
    }

    @RequestMapping(value = "/season/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getSeasonStats(@PathVariable String id) {
        Season season = leagueService.findOne(new Season(id));
        List<Stat> stats = statService.getTeamStats().parallelStream().filter(s->s.getSeason().equals(season)).sorted(new Comparator<Stat>() {
            @Override
            public int compare(Stat o1, Stat o2) {
                return o1.getTeam().getRank().compareTo(o2.getTeam().getRank());
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
         return statService.getUserSeasonStats().get(season);
    }

    @RequestMapping(value = "/user/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public List<Stat> getUserStats(@PathVariable String id) {
        User u = leagueService.findOne(new User(id));
        final List<Stat> userStats = new ArrayList<>();

        statService.getUserSeasonStats().values().stream().parallel().forEach(new Consumer<List<Stat>>() {
            @Override
            public void accept(List<Stat> stats) {
                stats.parallelStream().filter(st -> st.getUser().equals(u)).forEach(userStats::add);
            }
        });
        userStats.sort(new Comparator<Stat>() {
            @Override
            public int compare(Stat o1, Stat o2) {
                if (o1.getSeason().isChallenge()) {
                    return -1;
                }
                if (o2.getSeason().isChallenge()) {
                    return 1;
                }
                return o2.getSeason().getStartDate().compareTo(o1.getSeason().getStartDate());
            }
        });
        userStats.add(Stat.buildLifeTimeStats(u, userStats));
        return userStats;
    }

    @RequestMapping(value = "/user/{id}/{seasonId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public Stat getUserStatsSeason(@PathVariable String id, @PathVariable String seasonId) {
        User u = leagueService.findOne(new User(id));
        Season s = leagueService.findOne(new Season(seasonId));
        List<Stat> stats = statService.getUserSeasonStats().get(s);
        for (Stat stat : stats) {
            if (stat.getUser().equals(u) && stat.getSeason().equals(s))
                return stat;
        }
        Stat stat =  new Stat();
        stat.setUser(u);
        stat.setSeason(s);
        return stat;
    }
}
