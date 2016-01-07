package com.society.admin.resources;

import com.society.admin.model.MatchModel;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ScheduleResource extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "/schedule/{seasonId}")
    public String getSchedule(@PathVariable String seasonId, @RequestParam(required = false) String teamId, Model model) {
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
        if (teamId == null)
            model.addAttribute("team", team);
        else
            model.addAttribute("team", teamApi.get(teamId));

        model.addAttribute("teams",teamApi.getBySeason(seasonId));
        model.addAttribute("season",seasonApi.get(seasonId));

        if (teamId == null) {
            model.addAttribute("teamMatches", matches);
            return "schedule/schedule";
        }

        List<MatchModel> teamMatches = MatchModel.fromTeam(teamMatchApi.getTeamMatchByTeam(teamId).stream().sorted(new Comparator<TeamMatch>() {
            @Override
            public int compare(TeamMatch o1, TeamMatch o2) {
                return o1.getMatchDate().compareTo(o2.getMatchDate());
            }
        }).collect(Collectors.toList()));

        for (MatchModel teamMatch : teamMatches) {
            teamMatch.setPlayerResults(playerResultApi.getPlayerResultsSummary(teamMatch.getId()));
        }
        model.addAttribute("teamMatches", teamMatches);
        return "schedule/scheduleTeam";
    }

}
