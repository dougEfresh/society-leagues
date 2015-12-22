package com.society.admin.resources;

import com.society.leagues.client.api.domain.Division;
import com.society.leagues.client.api.domain.Stat;
import com.society.leagues.client.api.domain.StatType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StatResource  extends BaseController {

    @RequestMapping(value = {"/stats/{userId}"}, method = RequestMethod.GET)
    public String list(@PathVariable String userId, Model model) {
        List<Stat> stats = statApi.getUserStats(userId);
        model.addAttribute("statUser",userApi.get(userId));
        model.addAttribute("topgunStats",   stats.stream().filter(s -> s.getSeason() != null && s.getSeason().isChallenge()).collect(Collectors.toList()));
        model.addAttribute("thursdayStats", stats.stream().filter(s -> s.getSeason() != null && s.getSeason().getDivision() == Division.EIGHT_BALL_THURSDAYS).collect(Collectors.toList()));
        model.addAttribute("wednesdayStats",stats.stream().filter(s -> s.getSeason() != null && s.getSeason().getDivision() == Division.EIGHT_BALL_WEDNESDAYS).collect(Collectors.toList()));
        model.addAttribute("tuesdayStats",  stats.stream().filter(s -> s.getSeason() != null && s.getSeason().getDivision() == Division.NINE_BALL_TUESDAYS).collect(Collectors.toList()));

        model.addAttribute("scrambleEightStats", stats.stream().filter(s -> s.getSeason() != null && s.getSeason().isScramble() && s.getType() == StatType.MIXED_EIGHT).collect(Collectors.toList()));
        model.addAttribute("scrambleNineStats", stats.stream().filter( s -> s.getSeason() != null && s.getSeason().isScramble() && s.getType() == StatType.MIXED_NINE).collect(Collectors.toList()));
        return "stats/userStats";
    }

    @RequestMapping(value = {"/stats"}, method = RequestMethod.GET)
    public String home(Model model) {
        return "stats/userStats";
    }
}
