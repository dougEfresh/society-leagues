package com.society.admin.resources;

import com.society.leagues.client.api.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
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


        model.addAttribute("scrambleNineStatsLifetime", stats.stream().filter(s->s.getType() == StatType.LIFETIME_NINE_BALL_SCRAMBLE).findFirst().orElse(null));
        model.addAttribute("scrambleNineStats",
                stats.stream()
                        .filter(s -> s.getSeason() != null)
                        .filter(s -> s.getSeason().isScramble())
                        .filter(s -> s.getType() == StatType.MIXED_NINE)
                        .collect(Collectors.toList())
        );

        model.addAttribute("lifetimeStats", stats.stream().filter(s->s.getType() == StatType.ALL).findFirst().orElse(null));
        return "stats/userStats";
    }

    @ModelAttribute
    public void setDisplay(Model model) {
      model.addAttribute("display",false);
    }

    @RequestMapping(value = {"/stats/{userId}/{seasonId}"}, method = RequestMethod.GET)
    public String list(@PathVariable String userId, @PathVariable String seasonId , Model model) {
        Season s = seasonApi.get(seasonId);
        User u = userApi.get(userId);
        model.addAttribute("season",s);
        List<PlayerResult> results = playerResultApi.getResults(userId,seasonId);
        for (PlayerResult result : results) {
            result.setReferenceUser(u);
        }
        model.addAttribute("results",results);
        model.addAttribute("resultUser",u);

        model.addAttribute("stats",statApi.getUserStats(userId).stream()
                .filter(st->st.getSeason() != null)
                .filter(st->st.getSeason().equals(s))
                .findFirst().orElse(null));
        return stats(userId,model);
    }


    @RequestMapping(value = {"/stats"}, method = RequestMethod.GET)
    public String home(Model model) {
        return "stats/userStats";
    }
}
