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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ScheduleResource extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "/schedule/{seasonId}")
    public String getSchedule(@PathVariable String seasonId, @RequestParam(required = false) String teamId, Model model) {
        //Map<String,List<TeamMatch>> matches = teamMatchApi.matchesBySeason(seasonId);
        List<TeamMatch> matches = teamMatchApi.matchesBySeasonList(seasonId);
        Season season = seasonApi.get(seasonId);
        model.addAttribute("season", season);
        LocalDate yest = LocalDateTime.now().minusDays(1).toLocalDate();
        if (season.isChallenge()) {
            matches = matches.stream().filter(tm->tm.getMatchDate().toLocalDate().isAfter(yest)).collect(Collectors.toList());
        }
        Map<String,List<TeamMatch>> sortedMatches = new TreeMap<>((Comparator) (o1, o2) -> {
            if (season.isChallenge()) {
                return o2.toString().compareTo(o1.toString());
            }
            return o1.toString().compareTo(o2.toString());
        });
        sortedMatches = matches.stream().collect(Collectors.groupingBy(t->t.getMatchDate().toLocalDate().toString()));

        model.addAttribute("userTeam",
                teamApi.userTeams(user.getId()).stream()
                .filter(t->t.getSeason().getId().equals(seasonId))
                .filter(t->t.hasUser(user)).findFirst().orElse(new Team("-1")));

        List<Team> teams = new ArrayList<>();
        teams.addAll(statApi.teamSeasonStats(seasonId));
        model.addAttribute("teams",teams.stream().filter(t->season.isChallenge() && !t.isDisabled()).collect(Collectors.toList()));

        if (teamId == null  || teamId.equals("-1")) {
            model.addAttribute("team", new Team("-1"));
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
            for (String s : sortedMatches.keySet()) {
                List<MatchModel> teamMatches = new ArrayList<>();
                for (TeamMatch teamMatch : sortedMatches.get(s)) {
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
        model.addAttribute("team", teamApi.get(teamId));
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
