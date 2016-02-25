package com.society.leagues.resources;

import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.model.MatchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeResource extends BaseController {

    @Autowired ChallengeApi challengeApi;
    @Autowired ChallengeResource challengeResource;

    @RequestMapping(value = {"/home"}, method = RequestMethod.GET)
    public String home(Model model, HttpServletResponse response) throws IOException {
        Map<Season,List<Stat>> topPlayers = new TreeMap<>(Season.sortOrder);
        User user = getUser(model);
        user.getSeasons().stream()
                .filter(Season::isActive)
                .collect(Collectors.toList())
                .forEach(s-> topPlayers.put(s,
                        statApi.getUserSeasonStats(s.getId())
                                .stream()
                                .filter(st->st.getType() == StatType.USER_SEASON)
                                .sorted(Stat.sortSeasonStats())
                                .limit(5)
                                .collect(Collectors.toList()))
        );
        model.addAttribute("topPlayers",topPlayers);
        LocalDateTime yesterday  = LocalDate.now().minusDays(1).atStartOfDay();
        List<Team> userTeams = teamApi.userTeams(user.getId());

        List<MatchModel> modelList = new ArrayList<>();
        for (Team userTeam : userTeams) {
            modelList.addAll(MatchModel.fromTeam(
                    teamMatchApi.getTeamMatchByTeam(userTeam.getId()).stream()
                    .filter(t->t.getMatchDate().isAfter(yesterday))
                    .sorted(TeamMatch.sortAcc())
                    .limit(3).collect(Collectors.toList())));

        }
        for (MatchModel matchModel : modelList) {
            matchModel.getHome().setMembers(teamApi.members(matchModel.getHome().getId()));
            matchModel.getAway().setMembers(teamApi.members(matchModel.getAway().getId()));
        }
        model.addAttribute("upcomingMatches", modelList);
        model.addAttribute("userStats",statApi.getUserStatsSummary(user.getId()).stream()
                .filter(s->s.getType() == StatType.USER_SEASON)
                .filter(s->s.getSeason().isActive())
                .collect(Collectors.toList()));


        model.addAttribute("userTeams",teamApi.userTeams(user.getId()));
        if (user.isChallenge()) {
            challengeResource.challenge(user,ChallengeResource.broadcast.getId(),LocalDate.now().toString(),model,response);
        }
        return "home/home";
    }
}
