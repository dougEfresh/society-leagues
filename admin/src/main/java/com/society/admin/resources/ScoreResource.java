package com.society.admin.resources;

import com.society.admin.exception.ApiException;
import com.society.admin.model.TeamMatchModel;
import com.society.leagues.client.api.SeasonApi;
import com.society.leagues.client.api.TeamApi;
import com.society.leagues.client.api.TeamMatchApi;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.TeamMatch;
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

    @RequestMapping(value = {"/scores"}, method = RequestMethod.GET)
    public String edit(Model model) {
        model.addAttribute("seasons",seasonApi.active());
        return "scores/challengeScores";
    }

    private String processScoreView(String seasonId, String date, Model model) {
        model.addAttribute("seasons",seasonApi.active());
        Map<String,List<TeamMatch>> matches = teamMatchApi.matchesBySeason(seasonId);

        String d = date == null ? matches.keySet().iterator().next() : date;
        model.addAttribute("dates", matches.keySet());
        model.addAttribute("date", d);
        model.addAttribute("model", new TeamMatchModel(matches.get(d)));
        model.addAttribute("teams", teamApi.getBySeason(seasonId));
        model.addAttribute("season",seasonApi.get(seasonId));
        return "scores/challengeScores";
    }

    @RequestMapping(value = {"/scores/{seasonId}"}, method = RequestMethod.GET)
    public String editDate(@PathVariable String seasonId, Model model) {
        return processScoreView(seasonId,null,model);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{date}"}, method = RequestMethod.GET)
    public String editDate(@PathVariable String seasonId, @PathVariable String date, Model model) {
        return processScoreView(seasonId,date,model);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{date}"}, method = RequestMethod.POST)
    public String save(@PathVariable String seasonId, @PathVariable String date, @ModelAttribute TeamMatchModel teamMatchModel, Model model) {
        try {
            teamMatchApi.save(teamMatchModel.getMatches());
            model.addAttribute("save","success");
            return processScoreView(seasonId,date,model);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            model.addAttribute("save","error");
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            model.addAttribute("error",errors.toString());
            return processScoreView(seasonId,date,model);
        }
    }

}