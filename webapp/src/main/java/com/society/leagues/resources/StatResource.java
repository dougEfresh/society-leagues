package com.society.leagues.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Lists;
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
    public String stats(@PathVariable String userId,
                        @RequestParam(required = false , defaultValue = "-1") String seasonId,
                        @RequestParam(required = false , defaultValue = "bar") String chartType,
                        @RequestParam(required = false , defaultValue = "all") String scrambleType,
                        Model model) {

        model.addAttribute("chartType",chartType);
        model.addAttribute("statUser",userApi.get(userId));
        model.addAttribute("statSeasons",seasonApi.active().stream().filter(s->!s.isChallenge()).filter(s->!s.isScramble()).collect(Collectors.toList()));
        List<Stat> userStats = statApi.getUserStatsSummary(userId);
        model.addAttribute("stats", userStats.stream().filter(s->s.getType() == StatType.USER_SEASON).sorted(Stat.sortSeasonStats()).collect(Collectors.toList()));
        model.addAttribute("scrambleStats",userStats.stream().filter(s->s.getType().isScramble()).collect(Collectors.toList()));
        model.addAttribute("season",seasonId.equals("-1") ? Season.getDefault() : seasonApi.get(seasonId));
        return "stats/userStats";
    }


    @RequestMapping(value = {"/stats/lifetime/scramble/{userId}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<StatLifeTime> scrambleLifetime(@PathVariable String userId) {
        return get(statApi.getUserStatsSummary(userId).stream().filter(s->s.getType().isScramble()).collect(Collectors.toList()));
    }

    @RequestMapping(value = {"/stats/lifetime/handicap/{userId}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<StatLifeTime> handicapLifetime(@PathVariable String userId, @RequestParam(required = false, defaultValue = "-1") String seasonId) {
        Season season = seasonId.equals("-1") ?  Season.getDefault() : seasonApi.get(seasonId);
        return get(statApi.getUserStatsSummary(userId).stream().filter(s->s.getType().isScramble()).collect(Collectors.toList()));
    }

    private List<StatLifeTime> get(List<Stat> stats) {
        List<StatLifeTime> lifeTimes = Lists.newArrayListWithCapacity(stats.size());
        for (Stat stat : stats) {
            StatLifeTime sf = new StatLifeTime(stat.getStatSeason());
            sf.addWins(stat.getWins());
            sf.addLost(stat.getLoses());
            lifeTimes.add(sf);
        }
        return lifeTimes.stream().sorted(sorter).collect(Collectors.toList());
    }

    final static Comparator<StatLifeTime> sorter = (o1, o2) -> {
        if (o1.getSs().getYear().equals(o2.getSs().getYear())) {
            return o1.getSs().getType().getOrder().compareTo(o2.getSs().getType().getOrder());
        }
        return o1.getSs().getYear().compareTo(o2.getSs().getYear());
    };

    @RequestMapping(value = {"/stats/lifetime/{userId}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<StatLifeTime> lifetime(@PathVariable String userId, @RequestParam(required = false , defaultValue = "-1") String seasonId) {
        List<StatLifeTime> stats  = new ArrayList<>();
        LocalDate now = LocalDate.now().plusWeeks(1);
        Season season = seasonId.equals("-1") ?  Season.getDefault() : seasonApi.get(seasonId);


        if (Season.getDefault().equals(season)) {
            Map<StatSeason, List<Stat>> userStats = statApi.getUserStatsSummary(userId).stream()
                    .filter(s -> s.getType() == StatType.USER_SEASON)
                    .filter(s -> !s.getSeason().isChallenge())
                    .filter(s -> s.getSeason().getsDate() != null)
                    .filter(s -> s.getSeason().getsDate().isBefore(now))
                    .filter(s -> !s.getSeason().isActive())
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
            return stats.stream().sorted(sorter).collect(Collectors.toList());

        }

        return get(statApi.getUserStatsSummary(userId).stream()
                    .filter(s -> s.getType() == StatType.USER_SEASON)
                    .filter(s-> s.getSeason() != null)
                    .filter(s -> !s.getSeason().isChallenge())
                    .filter(s -> s.getSeason().getsDate() != null)
                    .filter(s -> s.getSeason().getsDate().isBefore(now))
                    .filter(s-> s.getSeason().getDay().equals(season.getDay()))
                    .filter(s -> !s.getSeason().isActive())
                    .sorted((o1, o2) -> o1.getSeason().getsDate().compareTo(o2.getSeason().getsDate()))
                    .collect(Collectors.toList()));
    }

    @RequestMapping(value = {"/stats"}, method = RequestMethod.GET)
    public String home(Model model) {
        return "redirect:/app/stats/" + user.getId();
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
}
