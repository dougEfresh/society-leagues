package com.society.admin.resources;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class ScheduleResource extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "/schedule/{seasonId}")
    public String getSchedule(@PathVariable String seasonId, Model model) {
        Map<String,List<TeamMatch>> matches = teamMatchApi.matchesBySeason(seasonId);
        int maxGames = 0;
        for (String s : matches.keySet()) {
            if (maxGames < matches.get(s).size()) {
                maxGames = matches.get(s).size();
            }
        }
        final User u = model.containsAttribute("user") ? (User) model.asMap().get("user") : userApi.get();

        Team team = teamApi.userTeams(u.getId()).stream()
                .filter(t->t.getSeason().getId().equals(seasonId))
                .filter(t->t.hasUser(u)).findFirst().orElse(new Team("-1"));
        Season season = seasonApi.get(seasonId);
        if (season.isChallenge()) {
            Map<String,List<TeamMatch>> challenges = new HashMap<>();
            List<String> dates  =  matches.keySet().stream().sorted(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o2.compareTo(o1);
                }
            }).collect(Collectors.toList());

            for (String date : dates) {
                challenges.put(date,matches.get(date));
            }
            matches = challenges;
        }
        model.addAttribute("maxHeight",maxGames*65);
        model.addAttribute("team",team);
        model.addAttribute("season",seasonApi.get(seasonId));
        model.addAttribute("teamMatches", matches);
        return "schedule/schedule";
    }

}
