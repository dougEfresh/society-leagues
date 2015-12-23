package com.society.admin.resources;

import com.society.leagues.client.api.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class StatResource  extends BaseController {

    @RequestMapping(value = {"/stats/{userId}"}, method = RequestMethod.GET)
    public String stats(@PathVariable String userId, Model model) {
        List<Stat> stats = statApi.getUserStats(userId);
        model.addAttribute("statUser",userApi.get(userId));
        List<Stat> divisionStats = new ArrayList<>();

        model.addAttribute("topgunStats",   stats.stream().filter(s -> s.getSeason() != null && s.getSeason().isChallenge()).collect(Collectors.toList()));

        divisionStats.addAll(stats.stream().filter(s -> s.getSeason() != null && s.getSeason().getDivision() == Division.EIGHT_BALL_THURSDAYS).collect(Collectors.toList()));
        divisionStats.addAll(stats.stream().filter(s->s.getType() == StatType.LIFETIME_EIGHT_BALL_THURSDAY).collect(Collectors.toList()));

        model.addAttribute("thursdayStats", new ArrayList<>(divisionStats));

        divisionStats.clear();
        divisionStats.addAll(stats.stream().filter(s -> s.getSeason() != null && s.getSeason().getDivision() == Division.EIGHT_BALL_WEDNESDAYS).collect(Collectors.toList()));
        divisionStats.addAll(stats.stream().filter(s->s.getType() == StatType.LIFETIME_EIGHT_BALL_WEDNESDAY).collect(Collectors.toList()));
        model.addAttribute("wednesdayStats", new ArrayList<>(divisionStats));


        divisionStats.clear();
        divisionStats.addAll(stats.stream().filter(s -> s.getSeason() != null && s.getSeason().getDivision() == Division.NINE_BALL_TUESDAYS).collect(Collectors.toList()));
        divisionStats.addAll(stats.stream().filter(s->s.getType() == StatType.LIFETIME_NINE_BALL_TUESDAY).collect(Collectors.toList()));
        model.addAttribute("tuesdayStats", new ArrayList<>(divisionStats));

        divisionStats.clear();
        divisionStats.addAll(stats.stream().filter( s -> s.getSeason() != null && s.getSeason().isScramble() && s.getType() == StatType.MIXED_EIGHT)
                .collect(Collectors.toList()));
        divisionStats.addAll(stats.stream().filter(s->s.getType() == StatType.LIFETIME_EIGHT_BALL_SCRAMBLE).collect(Collectors.toList()));
        model.addAttribute("scrambleEightStats", new ArrayList<>(divisionStats));

        divisionStats.clear();
        divisionStats.addAll(stats.stream().filter( s -> s.getSeason() != null && s.getSeason().isScramble() && s.getType() == StatType.MIXED_NINE)
                .collect(Collectors.toList()));
        divisionStats.addAll(stats.stream().filter(s->s.getType() == StatType.LIFETIME_NINE_BALL_SCRAMBLE).collect(Collectors.toList()));

        model.addAttribute("scrambleNineStats", new ArrayList<>(divisionStats));

        return "stats/userStats";
    }
    @RequestMapping(value = {"/stats/{userId}/{seasonId}"}, method = RequestMethod.GET)
    public String list(@PathVariable String userId, @PathVariable String seasonId , Model model) {
        Season s = seasonApi.get(seasonId);
        User u = userApi.get(userId);
        model.addAttribute("season",s);
        List<PlayerResult> results = resultApi.resultsBySeason(userId,seasonId);
        for (PlayerResult result : results) {
            result.setReferenceUser(u);
        }
        model.addAttribute("results",results);
        return stats(userId,model);
    }


    @RequestMapping(value = {"/stats"}, method = RequestMethod.GET)
    public String home(Model model) {
        return "stats/userStats";
    }
}