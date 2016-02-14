package com.society.leagues.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.society.leagues.client.api.domain.*;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StatResource  extends BaseController {

    @RequestMapping(value = {"/stats/{userId}"}, method = RequestMethod.GET)
    public String stats(@PathVariable String userId, Model model) {
        List<Stat> stats = new ArrayList<>();
        for (Stat userStat : userStats) {
            if (userStat.getType() == StatType.USER_SEASON)
                stats.add(userStat);
        }
        model.addAttribute("statUser",userApi.get(userId));
        model.addAttribute("stats",stats);
        /*
        model.addAttribute("topgunStats",   stats.stream().filter(s -> s.getSeason() != null && s.getSeason().isChallenge()).collect(Collectors.toList()));

        model.addAttribute("thursdayStats", stats.stream().filter(s -> s.getSeason() != null && s.getSeason().getDivision() == Division.EIGHT_BALL_THURSDAYS).collect(Collectors.toList()));
        model.addAttribute("thursdayStatsLifetime", stats.stream().filter(s->s.getType() == StatType.LIFETIME_EIGHT_BALL_THURSDAY).findFirst().orElse(null));

        model.addAttribute("wednesdayStats", stats.stream().filter(s -> s.getSeason() != null && s.getSeason().getDivision() == Division.EIGHT_BALL_WEDNESDAYS).collect(Collectors.toList()));
        model.addAttribute("wednesdayStatsLifetime", stats.stream().filter(s->s.getType() == StatType.LIFETIME_EIGHT_BALL_WEDNESDAY).findFirst().orElse(null));

        model.addAttribute("tuesdayStatsLifetime",stats.stream().filter(s->s.getType() == StatType.LIFETIME_NINE_BALL_TUESDAY).findFirst().orElse(null));
        model.addAttribute("tuesdayStats", stats.stream().filter(s -> s.getSeason() != null && s.getSeason().getDivision() == Division.NINE_BALL_TUESDAYS).collect(Collectors.toList()));

        model.addAttribute("scrambleEightStatsLifetime", stats.stream().filter(s->s.getType() == StatType.LIFETIME_EIGHT_BALL_SCRAMBLE).findFirst().orElse(null));
        model.addAttribute("scrambleEightStats",
                 stats.stream()
                        .filter(s -> s.getSeason() != null)
                        .filter(s -> s.getSeason().isScramble())
                        .filter(s -> s.getType() == StatType.MIXED_EIGHT)
                         .collect(Collectors.toList()));


        model.addAttribute("scrambleNineStatsLifetime", stats.stream()
                .filter(s->s.getType() == StatType.LIFETIME_NINE_BALL_SCRAMBLE).findFirst().orElse(null)
        );
        model.addAttribute("scrambleNineStats",
                stats.stream()
                        .filter(s -> s.getSeason() != null)
                        .filter(s -> s.getSeason().isScramble())
                        .filter(s -> s.getType() == StatType.MIXED_NINE)
                        .collect(Collectors.toList())
        );
        */

        //model.addAttribute("lifetimeStats", stats.stream().filter(s->s.getType() == StatType.ALL).findFirst().orElse(null));
        return "stats/userStats";
    }

    public static class StatLifeTime {
        @JsonIgnore
        StatSeason ss;
        int win;
        int lost;

        public String getSeason() {
            return ss.getName();
        }

        @JsonIgnore
        public StatSeason getSs() {
            return ss;
        }

        public int getLost() {
            return lost;
        }

        public int getWin() {
            return win;
        }

        public StatLifeTime(StatSeason ss) {
            this.ss = ss;
        }
        public void addWins(int wins){
            this.win += wins;
        }

        public void addLost(int lost){
            this.lost += lost;
        }
        public StatLifeTime() {
        }
    }

    @RequestMapping(value = {"/stats/lifetime/{userId}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody public List<StatLifeTime> lifetime(@PathVariable String userId) {
        List<StatLifeTime> stats  = new ArrayList<>();
        LocalDate now = LocalDate.now().plusWeeks(1);
        Map<StatSeason,List<Stat>> userStats = statApi.getUserStatsSummary(userId).stream()
                .filter(s->s.getType() == StatType.USER_SEASON)
                .filter(s->!s.getSeason().isChallenge())
                .filter(s->s.getSeason().getsDate() != null)
                .filter(s->s.getSeason().getsDate().isBefore(now))
                .filter(s->!s.getSeason().isActive())
                .sorted((o1, o2) -> o1.getSeason().getsDate().compareTo(o2.getSeason().getsDate()))
                .collect(Collectors.groupingBy(Stat::getStatSeason));

        for (StatSeason ss : userStats.keySet()) {
            StatLifeTime sf = new StatLifeTime(ss);
            for (Stat stat : userStats.get(ss)) {
                sf.addWins(stat.getWins());
                sf.addLost(stat.getLoses());
            }
            stats.add(sf);
        }
        return stats.stream().sorted((o1, o2) -> {
            if (o1.getSs().getYear().equals(o2.getSs().getYear())) {
                return o1.getSs().getType().getOrder().compareTo(o2.getSs().getType().getOrder());
            }
            return o1.getSs().getYear().compareTo(o2.getSs().getYear());
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = {"/stats"}, method = RequestMethod.GET)
    public String home(Model model) {
        return "stats/userStats";
    }
}
