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
                        @RequestParam(required = false , defaultValue = "-1") String nineId,
                        @RequestParam(required = false , defaultValue = "-1") String eightId,
                        @RequestParam(required = false , defaultValue = "all") String scrambleType,
                        Model model) {

        model.addAttribute("chartType","bar");
        model.addAttribute("statUser",userApi.get(userId));
        model.addAttribute("nineSeason",userApi.get(userId).getSeasons().stream().filter(s->s.getId().equals(nineId)).findAny().orElse(Season.getDefault()));
        model.addAttribute("eightSeason",userApi.get(userId).getSeasons().stream().filter(s->s.getId().equals(eightId)).findAny().orElse(Season.getDefault()));

        model.addAttribute("statSeasons",seasonApi.active().stream().collect(Collectors.toList()));
        List<Stat> userStats = statApi.getUserStatsSummary(userId);
        model.addAttribute("stats", userStats.stream().filter(s->s.getType() == StatType.USER_SEASON).sorted(Stat.sortSeasonStats()).collect(Collectors.toList()));
        model.addAttribute("scrambleStats",userStats.stream().filter(s->s.getType().isScramble()).collect(Collectors.toList()));
        model.addAttribute("season",seasonId.equals("-1") ? Season.getDefault() : seasonApi.get(seasonId));
        model.addAttribute("nineSeasons",userApi.get(userId).getSeasons().stream().filter(s->s.isNine()).collect(Collectors.toList()));
        model.addAttribute("eightSeasons",userApi.get(userId).getSeasons().stream().filter(s->!s.isNine()).collect(Collectors.toList()));
        return "stats/userStats";
    }


    @RequestMapping(value = {"/stats/lifetime/scramble/{userId}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<StatLifeTime> scrambleLifetime(@PathVariable String userId) {
        return get(statApi.getUserStatsSummary(userId).stream().filter(s->s.getType().isScramble()).collect(Collectors.toList()));
    }

    static class StatHandicap {
        Handicap handicap;
        int win;
        int lost;

        public StatHandicap() {
        }

        public StatHandicap(Handicap handicap) {
            this.handicap = handicap;
        }

        public void addWin(int wins) {
            this.win += wins;
        }

        public void addLost(int lost) {
            this.lost += lost;
        }

        public String getHandicap() {
            return handicap.getDisplayName();
        }

        public void setHandicap(Handicap handicap) {
            this.handicap = handicap;
        }

        public int getWin() {
            return win;
        }

        public void setWins(int wins) {
            this.win = wins;
        }

        public int getLost() {
            return lost;
        }

        public void setLost(int lost) {
            this.lost = lost;
        }
    }

    @RequestMapping(value = {"/stats/lifetime/handicap/nine/{userId}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<StatHandicap> handicapLifetimeNine(@PathVariable String userId, @RequestParam(required = false, defaultValue = "-1") String seasonId) {
        Season season = seasonId.equals("-1") ?  Season.getDefault() : seasonApi.get(seasonId);
        return getHandicapStats(season,userId,Arrays.asList(Division.NINE_BALL_CHALLENGE, Division.NINE_BALL_TUESDAYS));
    }

    @RequestMapping(value = {"/stats/lifetime/handicap/eight/{userId}"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<StatHandicap> handicapLifetimeEight(@PathVariable String userId, @RequestParam(required = false, defaultValue = "-1") String seasonId) {
        Season season = seasonId.equals("-1") ?  Season.getDefault() : seasonApi.get(seasonId);
        return getHandicapStats(season,userId,Arrays.asList(Division.EIGHT_BALL_THURSDAYS, Division.EIGHT_BALL_WEDNESDAYS, Division.MIXED_MONDAYS_MIXED));
     }

    private List<StatHandicap> getHandicapStats(Season season, String userId, List<Division> divisions) {
        List<Stat> stats = statApi.getUserStatsSummary(userId).stream().filter(s->s.getType() == StatType.HANDICAP)
                .filter(s-> divisions.contains(s.getSeason().getDivision()))
                .filter(s->s.getHandicap() != Handicap.UNKNOWN)
                .filter(s->s.getHandicap() != Handicap.NA)
                .collect(Collectors.toList());
        if (!season.equals(Season.getDefault())) {
            stats = stats.stream().filter(s->s.getSeason().equals(season)).collect(Collectors.toList());
        }
        List<StatHandicap> statHandicaps = new ArrayList<>();
        Map<Handicap,List<Stat>> statSeasons = stats.stream().collect(Collectors.groupingBy(Stat::getHandicap));
        for (Handicap hc: statSeasons.keySet()) {
            StatHandicap statHandicap = new StatHandicap(hc);
            for (Stat stat : statSeasons.get(hc)) {
                statHandicap.addWin(stat.getWins());
                statHandicap.addLost(stat.getLoses());
            }
            statHandicaps.add(statHandicap);
        }
        statHandicaps.sort(sortHandicap);
        return statHandicaps;
    }

    final static Comparator<StatHandicap> sortHandicap = (o1, o2) -> new Integer(o2.handicap.ordinal()).compareTo(o1.handicap.ordinal());

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
                    //.filter(s -> !s.getSeason().isActive())
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
                    //.filter(s -> !s.getSeason().isActive())
                    .sorted((o1, o2) -> o1.getSeason().getsDate().compareTo(o2.getSeason().getsDate()))
                    .collect(Collectors.toList()));
    }

    @RequestMapping(value = {"/stats"}, method = RequestMethod.GET)
    public String home(Model model) {
        return "redirect:/app/stats/" + getUser(model).getId();
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
