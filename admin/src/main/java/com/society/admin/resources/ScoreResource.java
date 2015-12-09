package com.society.admin.resources;

import com.society.leagues.client.api.SeasonApi;
import com.society.leagues.client.api.TeamMatchApi;
import com.society.leagues.client.api.domain.TeamMatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class ScoreResource extends BaseController {

    @Autowired TeamMatchApi teamMatchApi;
    @Autowired SeasonApi seasonApi;

    @RequestMapping(value = {"/scores"}, method = RequestMethod.GET)
    public String edit(Model model) {
        model.addAttribute("seasons",seasonApi.active());
        return "scores/challengeScores";
    }

    @RequestMapping(value = {"/scores/{seasonId}"}, method = RequestMethod.GET)
    public String editDate(@PathVariable String seasonId, @RequestParam(required = false) String date, Model model) {
        model.addAttribute("seasons",seasonApi.active());
        Map<String,List<TeamMatch>> matches = teamMatchApi.matchesBySeason(seasonId);

        if (date == null)
            model.addAttribute("date",matches.keySet().iterator().next());
        else
            model.addAttribute("date",date);

        model.addAttribute("results",matches.get(matches.keySet().iterator().next()));
        return "scores/challengeScores";
    }

}