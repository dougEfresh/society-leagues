package com.society.leagues.resources;

import com.society.leagues.model.MatchModel;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ScheduleResource extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "/schedule/{seasonId}")
    public String getSchedule(@PathVariable String seasonId, @RequestParam(required = false) String teamId, Model model) {
        //Map<String,List<TeamMatch>> matches = teamMatchApi.matchesBySeason(seasonId);
        Map<String,List<TeamMatch>> matches = teamMatchApi.matchesBySeasonSummary(seasonId);
        Season season = seasonApi.get(seasonId);
        if (season.isChallenge()) {
            LocalDateTime tenWeeks = LocalDateTime.now().minusWeeks(11);
            for (String s : matches.keySet()) {
                matches.put(s,matches.get(s).stream().filter(m->m.getMatchDate()
                        .isAfter(tenWeeks)).collect(Collectors.toList()
                )
                );
            }
            Map<String,List<TeamMatch>> orderMatches = new HashMap<>();
            for (String s : matches.keySet()) {
                if (!matches.get(s).isEmpty()) {
                    orderMatches.put(s,matches.get(s));
                }
            }
             Map<String,List<TeamMatch>> oMatches = new HashMap<>();
            for (String s : orderMatches.keySet()) {
                oMatches.put(s,orderMatches.get(s));
            }
            matches = oMatches;
        }
        Map<String,List<TeamMatch>> sortedMatches = new TreeMap<>(new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                if (season.isChallenge()) {
                    return o2.toString().compareTo(o1.toString());
                }
                return o1.toString().compareTo(o2.toString());
            }
        });
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
        Team team = new Team("-1");
        team.setName("...Choose a team...");
        model.addAttribute("userTeam",
                teamApi.userTeams(u.getId()).stream()
                .filter(t->t.getSeason().getId().equals(seasonId))
                .filter(t->t.hasUser(u)).findFirst().orElse(team));

        model.addAttribute("maxHeight",maxGames*45);
        if (teamId == null  || teamId.equals("-1")) {
            model.addAttribute("team", team);
        }
        else {
            team = teamApi.get(teamId);
            model.addAttribute(team);
        }
        List<Team> teams = new ArrayList<>();
        teams.add(team);
        teams.addAll(teamApi.seasonTeams(seasonId));
        model.addAttribute("teams",teams);
        model.addAttribute("team",team);
        model.addAttribute("season",seasonApi.get(seasonId));

        if (teamId == null || teamId.equals("-1")) {
            Map<String,List<MatchModel>> sorted = new TreeMap<>(new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    if (season.isChallenge()) {
                        return o2.toString().compareTo(o1.toString());
                    }
                    return o1.toString().compareTo(o2.toString());
                }
            });
            sorted.clear();
            for (String s : matches.keySet()) {
                List<MatchModel> teamMatches = new ArrayList<>();
                for (TeamMatch teamMatch : matches.get(s)) {
                    MatchModel matchModel = MatchModel.fromTeam(Arrays.asList(teamMatch)).iterator().next();
                    if (season.isChallenge()) {
                        teamMatch.getHome().setMembers(teamApi.members(teamMatch.getHome().getId()));
                        teamMatch.getAway().setMembers(teamApi.members(teamMatch.getAway().getId()));
                    }
                    matchModel.setPlayerResults(playerResultApi.getPlayerResultsSummary(matchModel.getId()));
                    teamMatches.add(matchModel);
                }
                teamMatches.sort(new Comparator<MatchModel>() {
                    @Override
                    public int compare(MatchModel o1, MatchModel o2) {
                        if (season.isChallenge()) {
                            if (o2.getMatchDate().toLocalDate().isEqual(o1.getMatchDate().toLocalDate())) {
                                return o1.getMatchDate().compareTo(o2.getMatchDate());
                            } else {
                                return o2.getMatchDate().compareTo(o1.getMatchDate());
                            }
                        }
                        return o1.getMatchDate().compareTo(o2.getMatchDate());
                    }
                });
                sorted.put(s,teamMatches);
            }
            model.addAttribute("teamMatches", sorted);
            return "schedule/schedule";
        }

        List<MatchModel> teamMatches = MatchModel.fromTeam(teamMatchApi.getTeamMatchByTeam(teamId));
        teamMatches.sort(new Comparator<MatchModel>() {
            @Override
            public int compare(MatchModel o1, MatchModel o2) {
                return o1.getMatchDate().compareTo(o2.getMatchDate());
            }
        });
        for (MatchModel teamMatch : teamMatches) {
            teamMatch.setPlayerResults(playerResultApi.getPlayerResultsSummary(teamMatch.getId()));
        }
        model.addAttribute("teamMatches", teamMatches);
        return "schedule/scheduleTeam";
    }

}
