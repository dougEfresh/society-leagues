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

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ScheduleResource extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "/schedule/{seasonId}")
    public String getSchedule(@PathVariable String seasonId, Model model) {
        Map<String,List<TeamMatch>> matches = teamMatchApi.matchesBySeason(seasonId);
        Map<String,List<TeamMatch>> sortedMatches = new TreeMap<>();
        for (String s : matches.keySet()) {
            sortedMatches.put(s,matches.get(s));
        }
        matches = sortedMatches;
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
            sortedMatches = new TreeMap<String,List<TeamMatch>>(new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    return o2.toString().compareTo(o1.toString());
                }
            });
            for (String s : matches.keySet()) {
                sortedMatches.put(s,matches.get(s));
            }
            matches = sortedMatches;
        }
        model.addAttribute("maxHeight",maxGames*45);
        model.addAttribute("team",team);
        model.addAttribute("season",seasonApi.get(seasonId));
        model.addAttribute("teamMatches", matches);
        return "schedule/schedule";
    }

}
