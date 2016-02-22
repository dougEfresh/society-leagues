package com.society.leagues.resources;

import com.society.leagues.client.api.domain.User;
import com.society.leagues.model.MatchModel;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMatch;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ScheduleResource extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "/schedule/{seasonId}")
    public String getSchedule(@PathVariable String seasonId, Model model) {
        //Map<String,List<TeamMatch>> matches = teamMatchApi.matchesBySeason(seasonId);
        List<TeamMatch> matches = teamMatchApi.matchesBySeasonList(seasonId);
        Season season = seasonApi.get(seasonId);
        model.addAttribute("season", season);
        LocalDate yest = LocalDateTime.now().minusDays(1).toLocalDate();
        User user = userApi.get();
        model.addAttribute("userTeam",
                teamApi.userTeams(userApi.get().getId()).stream()
                        .filter(t->t.getSeason().getId().equals(seasonId))
                        .filter(t->t.hasUser(user)).findFirst().orElse(new Team("-1"))
        );

        if (season.isChallenge()) {
            matches = matches.stream().filter(tm->tm.getMatchDate().toLocalDate().isAfter(yest)).collect(Collectors.toList());
        }

        Map<String,List<TeamMatch>> sortedMatches;
        sortedMatches = matches.stream().collect(Collectors.groupingBy(t->t.getMatchDate().toLocalDate().toString()));
        List<Team> teams = new ArrayList<>();
        teams.addAll(statApi.teamSeasonStats(seasonId));
        model.addAttribute("teams",teams.stream().filter(t->!t.isDisabled()).collect(Collectors.toList()));

        List<MatchModel> teamMatches;
        model.addAttribute("team", new Team("-1"));
        Map<String,List<MatchModel>> sorted = new TreeMap<>((Comparator) (o1, o2) -> {
            if (season.isChallenge()) {
                return o2.toString().compareTo(o1.toString());
            }
            return o1.toString().compareTo(o2.toString());
        });
        sorted.clear();
        for (String s : sortedMatches.keySet()) {
            teamMatches = new ArrayList<>();
            for (TeamMatch teamMatch : sortedMatches.get(s)) {
                MatchModel matchModel = MatchModel.fromTeam(Collections.singletonList(teamMatch)).iterator().next();
                    if (season.isChallenge()) {
                        teamMatch.getHome().setMembers(teamApi.members(teamMatch.getHome().getId()));
                        teamMatch.getAway().setMembers(teamApi.members(teamMatch.getAway().getId()));
                    }
                matchModel.setPlayerResults(playerResultApi.getPlayerResultsSummary(matchModel.getId()));
                teamMatches.add(matchModel);
            }
            teamMatches.sort((o1, o2) -> {
                if (season.isChallenge()) {
                    if (o2.getMatchDate().toLocalDate().isEqual(o1.getMatchDate().toLocalDate())) {
                        return o1.getMatchDate().compareTo(o2.getMatchDate());
                    } else {
                        return o2.getMatchDate().compareTo(o1.getMatchDate());
                    }
                }
                return o1.getMatchDate().compareTo(o2.getMatchDate());
            });
            sorted.put(s,teamMatches);
        }
        model.addAttribute("teamMatches", sorted);
        return "schedule/schedule";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/schedule/team/{teamId}")
    public String getTeamSchedule(@PathVariable String teamId, Model model) {
        Team team = teamApi.get(teamId);
        Season season = team.getSeason();
        getSchedule(season.getId(),model);
        model.addAttribute("team", team);
        List<MatchModel> teamMatches = MatchModel.fromTeam(teamMatchApi.getTeamMatchByTeam(teamId));
        teamMatches.stream().forEach(t->t.getHome().setMembers(teamApi.members(t.getHome().getId())));
        teamMatches.stream().forEach(t->t.getAway().setMembers(teamApi.members(t.getAway().getId())));
        teamMatches.sort((o1, o2) -> o1.getMatchDate().compareTo(o2.getMatchDate()));
        teamMatches.stream().forEach(teamMatch->teamMatch.setPlayerResults(playerResultApi.getPlayerResultsSummary(teamMatch.getId())));
        model.addAttribute("teamMatches", teamMatches);
        return "schedule/scheduleTeam";
    }

    @RequestMapping(method = RequestMethod.GET, value = "/schedule/team/available/{teamId}")
    public String getTeamAvailableSchedule(@PathVariable String teamId, Model model) {
        getTeamSchedule(teamId,model);
         List<MatchModel> teamMatches = (List<MatchModel>) model.asMap().get("teamMatches");
        LocalDateTime now = LocalDateTime.now().minusDays(1);
        model.addAttribute("teamMatches", teamMatches.stream().filter(t->t.getMatchDate().isAfter(now)).collect(Collectors.toList()));
        return "schedule/scheduleTeamAvailable";
    }

    @RequestMapping(method = RequestMethod.POST, value = "/schedule/team/available", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Map<String,Object>> updateTeamAvailable(@RequestBody  List<Map<String,Object>> notAvailables) {

        return  notAvailables;
    }
}
