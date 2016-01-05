package com.society.admin.resources;

import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.TeamMatch;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class ResultResource extends BaseController {


    @RequestMapping(value = {"/results/{matchId}"}, method = RequestMethod.GET)
    public String editResults(@PathVariable String matchId, Model model) {
        TeamMatch tm = teamMatchApi.get(matchId);
        List<PlayerResult> results = playerResultApi.getPlayerResultByTeamMatch(matchId);
        int homePoints = 0;
        int awayPoints = 0;
        for (PlayerResult result : results) {
            homePoints += result.getHomeRacks();
            awayPoints += result.getAwayRacks();
            result.setHomePoints(homePoints);
            result.setAwayPoints(awayPoints);
            result.setReferenceTeam(result.getTeamMatch().getWinner());
        }
        model.addAttribute("teamMatch",tm);
        model.addAttribute("results",results);
        model.addAttribute("season",tm.getSeason());

        return "results/teamMatchResults";
    }
}
