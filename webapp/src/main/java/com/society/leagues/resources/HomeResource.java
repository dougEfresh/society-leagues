package com.society.leagues.resources;

import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.model.MatchModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeResource extends BaseController {

    @Autowired ChallengeApi challengeApi;

    @RequestMapping(value = {"/home", "","/"}, method = RequestMethod.GET)
    public String home(Model model) {
        Map<Season,List<Stat>> topPlayers = new TreeMap<>(Season.sortOrder);
        user.getSeasons().stream().filter(Season::isActive).collect(Collectors.toList()).forEach(s->
                topPlayers.put(s,
                        statApi.getUserSeasonStats(s.getId())
                                .stream()
                                .filter(st->st.getType() == StatType.USER_SEASON)
                                .limit(5)
                                .collect(Collectors.toList()))
        );
        model.addAttribute("topPlayers",topPlayers);
        LocalDateTime yesterday  = LocalDate.now().minusDays(1).atStartOfDay();
        List<MatchModel> modelList = MatchModel.fromTeam(
                userMatches.stream()
                        .filter(t->t.getMatchDate().isAfter(yesterday))
                        .sorted(TeamMatch.sortAcc())
                        .limit(3).collect(Collectors.toList()));
        for (MatchModel matchModel : modelList) {
            matchModel.getHome().setMembers(teamApi.members(matchModel.getHome().getId()));
            matchModel.getAway().setMembers(teamApi.members(matchModel.getAway().getId()));
        }
        model.addAttribute("upcomingMatches", modelList);
        model.addAttribute("userStats",userStats.stream()
                .filter(s->s.getType() == StatType.USER_SEASON)
                .filter(s->s.getSeason().isActive())
                .collect(Collectors.toList()));
        return "home/home";
    }
}
