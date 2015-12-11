package com.society.admin.resources;

import com.society.admin.model.PlayerResultModel;
import com.society.admin.model.TeamMatchModel;
import com.society.leagues.client.api.PlayerResultApi;
import com.society.leagues.client.api.SeasonApi;
import com.society.leagues.client.api.TeamApi;
import com.society.leagues.client.api.TeamMatchApi;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

@Controller
public class ScoreResource extends BaseController {

    @Autowired TeamMatchApi teamMatchApi;
    @Autowired SeasonApi seasonApi;
    @Autowired TeamApi teamApi;
    @Autowired PlayerResultApi playerResultApi;

    @RequestMapping(value = {"/scores"}, method = RequestMethod.GET)
    public String edit(Model model) {
        model.addAttribute("seasons",seasonApi.active());
        return "scores/challengeScores";
    }

    private String processScoreView(String seasonId, String date, String matchId, Model model) {
        model.addAttribute("seasons",seasonApi.active());
        Map<String,List<TeamMatch>> matches = teamMatchApi.matchesBySeason(seasonId);

        String d = date == null ? matches.keySet().iterator().next() : date;
        model.addAttribute("dates", matches.keySet());
        model.addAttribute("date", d);
        model.addAttribute("model", new TeamMatchModel(matches.get(d)));
        model.addAttribute("teams", teamApi.getBySeason(seasonId));
        Season s = seasonApi.get(seasonId);
        model.addAttribute("season",s);

        if (matchId != null) {
            model.addAttribute("results", new PlayerResultModel(playerResultApi.getPlayerResultByTeamMatch(matchId),matchId));
            Map<String,List<User>> members = teamMatchApi.teamMembers(matchId);
            model.addAttribute("homeMembers",members.get("home"));
            model.addAttribute("awayMembers",members.get("away"));
        }

        if (s.isChallenge())
            return "scores/challengeScores";

        if (s.isNine())
            return "scores/nineScores";

        if (s.isScramble())
            return "scores/scrambleScores";

        return "scores/eightScores";
    }

    @RequestMapping(value = {"/scores/{seasonId}"}, method = RequestMethod.GET)
    public String editDate(@PathVariable String seasonId, Model model) {
        return processScoreView(seasonId,null,null,model);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{date}"}, method = RequestMethod.GET)
    public String editDate(@PathVariable String seasonId, @PathVariable String date, Model model) {
        return processScoreView(seasonId,date,null,model);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{date}/{matchId}"}, method = RequestMethod.GET)
    public String editResults(@PathVariable String seasonId, @PathVariable String date, @PathVariable String matchId, Model model) {
        return processScoreView(seasonId,date,matchId,model);
    }

    private String save(String seasonId, String date, String matchId, TeamMatchModel teamMatchModel, PlayerResultModel playerResultModel, Model model) {
        try {
            teamMatchApi.save(teamMatchModel.getMatches());
            if (playerResultModel != null )
                playerResultApi.save(playerResultModel.getPlayerResults());
            model.addAttribute("save","success");
            return processScoreView(seasonId,date,matchId,model);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            model.addAttribute("save","error");
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            model.addAttribute("error",errors.toString());
            return processScoreView(seasonId,date,matchId,model);
        }
    }

    @RequestMapping(value = {"/scores/{seasonId}/{date}"}, method = RequestMethod.POST)
    public String save(@PathVariable String seasonId, @PathVariable String date, @ModelAttribute TeamMatchModel teamMatchModel, Model model) {
        return save(seasonId,date,null,teamMatchModel,null,model);
    }
    @RequestMapping(value = {"/scores/{seasonId}/{date}/{matchId}"}, method = RequestMethod.POST)
    public String saveResults(@PathVariable String seasonId, @PathVariable String date, @PathVariable String matchId, @ModelAttribute TeamMatchModel teamMatchModel,@ModelAttribute PlayerResultModel playerResultModel, Model model) {
        return save(seasonId,date,matchId,teamMatchModel,playerResultModel,model);
    }


}